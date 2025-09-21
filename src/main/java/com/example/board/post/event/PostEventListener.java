package com.example.board.post.event;

import com.example.board.post.Post;
import com.example.board.post.file.FileAwsData;
import com.example.board.post.file.FileAwsDataRepository;
import com.example.board.post.file.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventListener {
    private final FileAwsDataRepository fileAwsDataRepository;
    private final S3Service s3Service;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostCreated(PostCreatedEvent event) {
        Post post = event.getPost();
        long start = System.currentTimeMillis(); // 시작 시간 기록
        List<Future<?>> futures = new ArrayList<>();

        for (Path path : event.getTempFilePaths()) {
            futures.add(executor.submit(() -> {

                try {
                    String key = s3Service.uploadFile(path.toFile());
                    String fileUrl = s3Service.getFileUrl(key);

                    FileAwsData fileAwsData = new FileAwsData();
                    fileAwsData.setFileName(path.getFileName().toString());
                    fileAwsData.setS3Key(key);
                    fileAwsData.setFileUrl(fileUrl);
                    fileAwsData.setPost(post);

                    fileAwsDataRepository.save(fileAwsData);

                    Files.deleteIfExists(path); // 임시 파일 삭제
                } catch (Exception e) {
                    log.error("파일 업로드 실패: {}", path.getFileName(), e);
                }
            }));
        }


        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                log.error("업로드 작업 중 예외 발생", e);
            }
        }
        long end = System.currentTimeMillis(); // 끝난 시간 기록
        log.info("=== 전체 업로드 완료! 총 소요 시간: {} ms ===", (end - start));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostDeleted(PostDeletedEvent event) {
        for (String key : event.getS3Keys()) {
            try {
                s3Service.deleteFile(key);
            } catch (Exception e) {
                log.error("S3 삭제 실패: {}", key, e);
            }
        }
    }
}
