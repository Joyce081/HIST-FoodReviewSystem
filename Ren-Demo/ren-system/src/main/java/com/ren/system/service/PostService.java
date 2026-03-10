
package com.ren.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ren.system.core.domain.entity.Post;

import java.util.List;

public interface PostService extends IService<Post> {

    /**
     * 添加岗位
     * @param post
     * @return int
     * @author ren
     * @date 2025/05/18 13:49
     */
    long addPost(Post post,String createBy);

    /**
     * 删除岗位
     * @param postId
     * @author ren
     * @date 2025/05/18 13:49
     */
    void removePost(long postId);

    /**
     * 编辑岗位
     * @param post
     * @author ren
     * @date 2025/05/18 13:49
     */
    void modifyPost(Post post,String updateBy);

    /**
     * 分页获取岗位列表
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.system.entity.Post>
     * @author ren
     * @date 2025/05/18 13:50
     */
    IPage<Post> listPostByPage(String searchLike);

    /**
     * 获取岗位详情
     * @param postId
     * @return com.ren.system.entity.Post
     * @author ren
     * @date 2025/05/18 13:50
     */
    Post getPostById(long postId);

    /**
     * 岗位列表
     * @param searchLike
     * @return java.util.List<com.ren.system.entity.Post>
     * @author ren
     * @date 2025/05/23 14:29
     */
	List<Post> listPostByParam(String searchLike);

    /**
     * 获取用户所有岗位列表
     * @param userId
     * @return java.util.List<com.ren.system.core.domain.entity.Post>
     * @author ren
     * @date 2025/09/20 14:16
     */
    List<Post> listPostByUserId(Long userId);
}