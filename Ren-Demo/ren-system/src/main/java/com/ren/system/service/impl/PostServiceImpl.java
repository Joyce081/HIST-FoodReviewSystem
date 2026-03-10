
package com.ren.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.PageUtils;
import com.ren.common.utils.StringUtils;
import com.ren.system.core.domain.entity.Post;
import com.ren.system.mapper.PostMapper;
import com.ren.system.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    /**
     * 添加岗位
     * @param post
     * @return int
     * @author ren
     * @date 2025/05/18 13:49
     */
    @Override
    public long addPost(Post post,String createBy) {
        post.setCreateBy(createBy);
        post.setCreateTime(DateUtils.currentSeconds());
        postMapper.insertPost(post);
        return post.getPostId();
    }

    /**
     * 删除岗位
     * @param postId
     * @author ren
     * @date 2025/05/18 13:49
     */
    @Override
    public void removePost(long postId) {
        postMapper.deletePost(postId);
    }

    /**
     * 编辑岗位
     * @param post
     * @author ren
     * @date 2025/05/18 13:49
     */
    @Override
    public void modifyPost(Post post,String updateBy) {
        post.setUpdateBy(updateBy);
        post.setUpdateTime(DateUtils.currentSeconds());
        postMapper.updatePost(post);
    }

    /**
     * 分页获取岗位列表
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.system.entity.Post>
     * @author ren
     * @date 2025/05/18 13:50
     */
    @Override
    public IPage<Post> listPostByPage(String searchLike) {
        if(StringUtils.isNotBlank(searchLike)){
            searchLike = StringUtils.format("%%{}%%",searchLike);
        }
        IPage<Post> postList = postMapper.listPostByPage(PageUtils.createPage(Post.class),searchLike);
        return postList;
    }

    /**
     * 获取岗位详情
     * @param postId
     * @return com.ren.system.entity.Post
     * @author ren
     * @date 2025/05/18 13:50
     */
    @Override
    public Post getPostById(long postId) {
        Post post = postMapper.selectById(postId);
        return post;
    }

    /**
     * 岗位列表
     * @param searchLike
     * @return java.util.List<com.ren.system.entity.Post>
     * @author ren
     * @date 2025/05/23 14:30
     */
    @Override
    public List<Post> listPostByParam(String searchLike) {
        if(StringUtils.isNotBlank(searchLike)){
            searchLike = StringUtils.format("%%{}%%",searchLike);
        }
        List<Post> postList = postMapper.listPostByParam(searchLike);
        return postList;
    }

    /**
     * 获取用户所有岗位列表
     * @param userId
     * @return java.util.List<com.ren.system.core.domain.entity.Post>
     * @author ren
     * @date 2025/09/20 14:16
     */
    @Override
    public List<Post> listPostByUserId(Long userId) {
        List<Post> postList = postMapper.listPostByUserId(userId);
        return postList;
    }

}