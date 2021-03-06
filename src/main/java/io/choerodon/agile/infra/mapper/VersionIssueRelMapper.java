package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
public interface VersionIssueRelMapper extends BaseMapper<VersionIssueRelDO> {

    int deleteIncompleteIssueByVersionId(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    int deleteByVersionIds(@Param("projectId") Long projectId, @Param("versionIds") List<Long> versionIds);

    /**
     * 通过issueId和Type批量删除版本关联（已归档的不删除）
     *
     * @param projectId   projectId
     * @param issueId     issueId
     * @param versionType versionType
     * @return int
     */
    int batchDeleteByIssueIdAndType(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("versionType") String versionType);
}