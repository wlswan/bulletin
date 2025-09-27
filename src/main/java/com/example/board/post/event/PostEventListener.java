package com.example.board.post.event;

import com.example.board.post.Post;
import com.example.board.post.file.FileAwsData;
import com.example.board.post.file.FileAwsDataRepository;
import com.example.board.post.file.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
public class PostEventListener {
    private final FileAwsDataRepository fileAwsDataRepository;
    private final S3Service s3Service;
    private final Executor executor;

    public PostEventListener(FileAwsDataRepository fileAwsDataRepository,
                             S3Service s3Service,
                             @Qualifier("fileUploadExecutor") Executor executor) {
        this.fileAwsDataRepository = fileAwsDataRepository;
        this.s3Service = s3Service;
        this.executor = executor;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostCreated(PostCreatedEvent event) {
        Post post = event.getPost();
        long start = System.currentTimeMillis();

        // 각 파일 업로드를 CompletableFuture로 실행
        List<CompletableFuture<FileAwsData>> futures = new ArrayList<>();

        for (Path path : event.getTempFilePaths()) {
            CompletableFuture<FileAwsData> future = CompletableFuture.supplyAsync(() -> {
                try {
                    String key = s3Service.uploadFile(path.toFile());
                    String fileUrl = s3Service.getFileUrl(key);

                    FileAwsData fileAwsData = new FileAwsData();
                    fileAwsData.setFileName(path.getFileName().toString());
                    fileAwsData.setS3Key(key);
                    fileAwsData.setFileUrl(fileUrl);
                    fileAwsData.setPost(post);

                    Files.deleteIfExists(path); // 임시 파일 삭제
                    return fileAwsData;
                } catch (Exception e) {
                    log.error("파일 업로드 실패: {}", path.getFileName(), e);
                    return null;
                }
            }, executor);

            futures.add(future);
        }

        // 모든 업로드 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 결과 모으기
        List<FileAwsData> results = futures.stream()
                .map(CompletableFuture::join)  // join = get() 과 비슷, 예외는 런타임으로 던짐
                .filter(f -> f != null)
                .toList();

        if (!results.isEmpty()) {
            fileAwsDataRepository.saveAll(results);
        }

        long end = System.currentTimeMillis();
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

