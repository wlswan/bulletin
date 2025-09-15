package com.example.board.post;

import com.example.board.exception.PostNotFoundException;
import com.example.board.post.file.FileAwsData;
import com.example.board.post.file.FileAwsDataRepository;
import com.example.board.post.file.S3Service;
import com.example.board.security.User;
import com.example.board.security.UserRepository;
import com.example.board.security.auth.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileAwsDataRepository fileAwsDataRepository;
    private final S3Service s3Service;

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(()->new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    public Post findPostWithComments(Long id) {
        return postRepository.findPostWithComments(id).orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public Post create(@Valid PostDto postdto, Long userId) {
        Post post = new Post();
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));
        post.setUser(user);
        post.setTitle(postdto.getTitle());
        post.setContent(postdto.getContent());

        Post savedPost =  postRepository.save(post); //영속 상태

        if (postdto.getFiles() != null) {
            for(MultipartFile file : postdto.getFiles()) {
                if (!file.isEmpty()) {
                    String key = s3Service.uploadFile(file);
                    String fileUrl = s3Service.getFileUrl(key);

                    FileAwsData fileAwsData = new FileAwsData();
                    fileAwsData.setFileName(file.getOriginalFilename());
                    fileAwsData.setS3Key(key);
                    fileAwsData.setFileUrl(fileUrl);
                    fileAwsData.setPost(savedPost);
                    savedPost.getFiles().add(fileAwsData); //더티 체킹이 됨 영속 상태여서
                }
            }
        }
        return savedPost;
    }

    @Transactional
    public void delete(Long postId, Long userId) {
        Post post = findById(postId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));

        if(isWriterOrAdmin(userId,post)){
            for (FileAwsData file : post.getFiles()) {
                s3Service.deleteFile(file.getS3Key());
            }
            postRepository.deleteById(postId);
        }
        else {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }
    }


    @Transactional
    public void update(Long id, PostDto postDto, List<Long> deleteFileIds, Long userId) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시물이 존재하지 않습니다."));

        if(!isWriterOrAdmin(userId,post)) {
            throw new AccessDeniedException("게시글에 수정할 권한이 없습니다.");
        }

        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            for (Long deleteFileId : deleteFileIds) {
                FileAwsData fileData = fileAwsDataRepository.findById(deleteFileId).orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));
                s3Service.deleteFile(fileData.getS3Key());
                post.getFiles().remove(fileData);

            }
        }

        if (postDto.getFiles() != null) {
            for (MultipartFile newFile : postDto.getFiles()) {
                if (newFile != null && !newFile.isEmpty()) {
                    String key = s3Service.uploadFile(newFile);
                    String fileUrl = s3Service.getFileUrl(key);

                    FileAwsData fileAwsData = new FileAwsData();
                    fileAwsData.setFileName(newFile.getOriginalFilename());
                    fileAwsData.setS3Key(key);
                    fileAwsData.setFileUrl(fileUrl);
                    fileAwsData.setPost(post);

                    post.getFiles().add(fileAwsData);
                }
            }
        }

        post.setContent(postDto.getContent());
        post.setTitle(postDto.getTitle());

    }


    public Page<Post> search(String type, String keyword, Pageable pageable) {
        switch(type) {
            case "title":
                return postRepository.findByTitleContaining(keyword, pageable);
            case "content":
                return postRepository.findByContentContaining(keyword, pageable);
            case "author":
                return postRepository.findByUser_EmailContaining(keyword, pageable);
            default:
                return postRepository.findAll(pageable);
        }
    }

    @Transactional
    public Post findByIdAndIncreaseViews(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));
        post.setViews(post.getViews()+1);
        return post;
    }

    public boolean isWriterOrAdmin(Long userId, Post post) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));
        return user.getRole() == Role.ROLE_ADMIN || post.getUser().getId().equals(user.getId());
    }

    public Page<Post> findHots(Pageable pageable) {
        return postRepository.findHots(pageable);
    }

    public Post findPostWithCommentsAndFiles(Long id) {
        Post postWithComments = postRepository.findPostWithComments(id)
                .orElseThrow(() -> new PostNotFoundException("게시글이 없습니다."));

        Post postWithFiles = postRepository.findPostWithFiles(id)
                .orElseThrow(() -> new PostNotFoundException("게시글이 없습니다."));

        postWithComments.setFiles(postWithFiles.getFiles());

        return postWithComments;
    }
}
