package io.choerodon.iam.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.domain.iam.entity.MemberRoleE;
import io.choerodon.iam.domain.repository.MemberRoleRepository;
import io.choerodon.iam.infra.dataobject.MemberRoleDO;
import io.choerodon.iam.infra.mapper.MemberRoleMapper;
import io.choerodon.iam.infra.mapper.OrganizationMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.RoleMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author superlee
 * @data 2018/3/29
 */
@Component
public class MemberRoleRepositoryImpl implements MemberRoleRepository {

    private MemberRoleMapper memberRoleMapper;

    private ProjectMapper projectMapper;

    private OrganizationMapper organizationMapper;

    private RoleMapper roleMapper;

    public MemberRoleRepositoryImpl(MemberRoleMapper memberRoleMapper,
                                    ProjectMapper projectMapper,
                                    OrganizationMapper organizationMapper,
                                    RoleMapper roleMapper) {
        this.memberRoleMapper = memberRoleMapper;
        this.projectMapper = projectMapper;
        this.organizationMapper = organizationMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public MemberRoleE insertSelective(MemberRoleE memberRoleE) {
        MemberRoleDO memberRoleDO = ConvertHelper.convert(memberRoleE, MemberRoleDO.class);
        if (memberRoleDO.getMemberType() == null) {
            memberRoleDO.setMemberType("user");
        }
        if (roleMapper.selectByPrimaryKey(memberRoleDO.getRoleId()) == null) {
            throw new CommonException("error.member_role.insert.role.not.exist");
        }
        if (ResourceLevel.PROJECT.value().equals(memberRoleDO.getSourceType())
                && projectMapper.selectByPrimaryKey(memberRoleDO.getSourceId()) == null) {
            throw new CommonException("error.member_role.insert.project.not.exist");
        }
        if (ResourceLevel.ORGANIZATION.value().equals(memberRoleDO.getSourceType())
                && organizationMapper.selectByPrimaryKey(memberRoleDO.getSourceId()) == null) {
            throw new CommonException("error.member_role.insert.organization.not.exist");
        }
        if (memberRoleMapper.selectOne(memberRoleDO) != null) {
            throw new CommonException("error.member_role.has.existed");
        }
        if (memberRoleMapper.insertSelective(memberRoleDO) != 1) {
            throw new CommonException("error.member_role.create");
        }
        return ConvertHelper.convert(
                memberRoleMapper.selectByPrimaryKey(memberRoleDO.getId()), MemberRoleE.class);
    }

    @Override
    public List<MemberRoleE> select(MemberRoleE memberRoleE) {
        MemberRoleDO memberRoleDO = ConvertHelper.convert(memberRoleE, MemberRoleDO.class);
        return ConvertHelper.convertList(memberRoleMapper.select(memberRoleDO), MemberRoleE.class);
    }

    @Override
    public void deleteById(Long id) {
        memberRoleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public MemberRoleE selectByPrimaryKey(Long id) {
        return ConvertHelper.convert(memberRoleMapper.selectByPrimaryKey(id), MemberRoleE.class);
    }

    @Override
    public MemberRoleE selectOne(MemberRoleE memberRole) {
        return ConvertHelper.convert(
                memberRoleMapper.selectOne(
                        ConvertHelper.convert(memberRole, MemberRoleDO.class)), MemberRoleE.class);
    }

    @Override
    public List<Long> selectDeleteList(final List<Long> deleteList, final long memberId,
                                       final long sourceId, final String sourceType) {
        return memberRoleMapper.selectDeleteList(memberId, sourceId, sourceType, deleteList);
    }
}
