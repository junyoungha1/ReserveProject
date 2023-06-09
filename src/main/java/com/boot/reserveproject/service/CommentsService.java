package com.boot.reserveproject.service;


import com.boot.reserveproject.domain.Board;
import com.boot.reserveproject.domain.Comments;
import com.boot.reserveproject.repository.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentsService {
    private final CommentsRepository commentsRepository;
    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }
    public void createOrUpdateComments(Comments comments){
        commentsRepository.save(comments);
    }
    public List<Comments> getCommentsByBoardNo(Long no){
        return commentsRepository.findCommentsByBoardNo(no);
    }
    public Long countAllComments(){
        return commentsRepository.countAllComments();
    }
    public Long findMaxRef(){
        return commentsRepository.findMaxRef();
    }
    public Comments findOneCommentByBoardNo(Long no){
        return commentsRepository.findOneCommentByBoardNo(no);
    }
    public Long findMaxLevelByRef(Long no){
        return commentsRepository.findMaxLevelByRef(no);
    }
    public void deleteComment(long no){
        commentsRepository.deleteById(no);
    }
    public Comments getOneCommentByCommentNo(long no){
        return commentsRepository.getOneCommentByCommentNo(no);
    }
    public void deleteCommentsBySameRef(long ref){
        commentsRepository.deleteCommentsBySameRef(ref);
    }
    public void deleteCommentsByLoginId(String loginId){
        commentsRepository.deleteCommentsByLoginId(loginId);
    }
}
