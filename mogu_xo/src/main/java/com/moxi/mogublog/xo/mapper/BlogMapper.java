package com.moxi.mogublog.xo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.moxi.mogublog.xo.entity.Blog;
import com.moxi.mougblog.base.mapper.SuperMapper;

/**
 * <p>
 * 博客表 Mapper 接口
 * </p>
 *
 * @author xuzhixiang
 * @since 2018-09-08
 */
public interface BlogMapper extends SuperMapper<Blog> {
	
	@Select("SELECT tag_uid, COUNT(*) as count FROM  t_blog GROUP BY tag_uid")
	List<Map<String, Object>> getBlogCountByTag();
}
