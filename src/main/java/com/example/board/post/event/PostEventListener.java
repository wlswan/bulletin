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

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventListener {
    private final FileAwsDataRepository fileAwsDataRepository;
    private final S3Service s3Service;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostCreated(PostCreatedEvent event) {
        Post post = event.getPost();
        for (Path path : event.getTempFilePaths()) {
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
        }
    }
}
