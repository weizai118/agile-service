package io.choerodon.agile.app.service.impl;


import io.choerodon.agile.api.dto.IssueCommentCreateDTO;
import io.choerodon.agile.api.dto.IssueCommentDTO;
import io.choerodon.agile.api.dto.IssueCommentUpdateDTO;
import io.choerodon.agile.app.assembler.IssueCommentAssembler;
import io.choerodon.agile.app.service.IssueCommentService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.domain.agile.entity.IssueCommentE;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.agile.domain.agile.repository.IssueCommentRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.IssueCommentDO;
import io.choerodon.agile.infra.mapper.IssueCommentMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueCommentServiceImpl implements IssueCommentService {

    private static final String FIELD_COMMENT = "Comment";

    @Autowired
    private IssueCommentRepository issueCommentRepository;
    @Autowired
    private IssueCommentAssembler issueCommentAssembler;
    @Autowired
    private IssueCommentMapper issueCommentMapper;
    @Autowired
    private DataLogRepository dataLogRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public IssueCommentDTO createIssueComment(Long projectId, IssueCommentCreateDTO issueCommentCreateDTO) {
        IssueCommentE issueCommentE = issueCommentAssembler.issueCommentCreateDtoToEntity(issueCommentCreateDTO);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        issueCommentE.setUserId(customUserDetails.getUserId());
        issueCommentE.setProjectId(projectId);
        return queryByProjectIdAndCommentId(projectId, issueCommentRepository.create(issueCommentE).getCommentId());
    }

    @Override
    public IssueCommentDTO updateIssueComment(IssueCommentUpdateDTO issueCommentUpdateDTO, List<String> fieldList) {
        if (fieldList != null && !fieldList.isEmpty()) {
            IssueCommentE issueCommentE = issueCommentRepository.update(issueCommentAssembler.
                    issueCommentUpdateDtoToEntity(issueCommentUpdateDTO), fieldList.toArray(new String[fieldList.size()]));
            return queryByProjectIdAndCommentId(issueCommentE.getProjectId(), issueCommentE.getCommentId());
        } else {
            return null;
        }
    }

    @Override
    public List<IssueCommentDTO> queryIssueCommentList(Long projectId, Long issueId) {
        return ConvertHelper.convertList(issueCommentMapper.queryIssueCommentList(projectId, issueId), IssueCommentDTO.class);
    }

    private void dataLogComment(Long projectId, IssueCommentDO originIssueComment) {
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(projectId);
        dataLogE.setIssueId(originIssueComment.getIssueId());
        dataLogE.setField(FIELD_COMMENT);
        dataLogE.setOldValue(originIssueComment.getCommentText());
        dataLogRepository.create(dataLogE);
    }

    private IssueCommentDO getCommentById(Long commentId) {
        IssueCommentDO issueCommentDO = issueCommentMapper.selectByPrimaryKey(commentId);
        if (issueCommentDO == null) {
            throw new CommonException("error.comment.get");
        }
        return issueCommentDO;
    }

    @Override
    public int deleteIssueComment(Long projectId, Long commentId) {
        IssueCommentDO originIssueComment = getCommentById(commentId);
        int result = issueCommentRepository.delete(projectId, commentId);
        dataLogComment(projectId, originIssueComment);
        return result;
    }

    @Override
    public int deleteByIssueId(Long issueId) {
        IssueCommentDO issueCommentDO = new IssueCommentDO();
        issueCommentDO.setIssueId(issueId);
        return issueCommentMapper.delete(issueCommentDO);
    }

    private IssueCommentDTO queryByProjectIdAndCommentId(Long projectId, Long commentId) {
        IssueCommentDO issueCommentDO = new IssueCommentDO();
        issueCommentDO.setProjectId(projectId);
        issueCommentDO.setCommentId(commentId);
        IssueCommentDTO issueCommentDTO = ConvertHelper.convert(issueCommentMapper.selectOne(issueCommentDO), IssueCommentDTO.class);
        issueCommentDTO.setUserName(userRepository.queryUserNameByOption(issueCommentDTO.getUserId(), true).getRealName());
        issueCommentDTO.setUserImageUrl(userRepository.queryUserNameByOption(issueCommentDTO.getUserId(), true).getImageUrl());
        return ConvertHelper.convert(issueCommentMapper.selectOne(issueCommentDO), IssueCommentDTO.class);
    }
}