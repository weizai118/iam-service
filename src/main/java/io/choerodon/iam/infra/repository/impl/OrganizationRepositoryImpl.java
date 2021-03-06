package io.choerodon.iam.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.domain.iam.entity.OrganizationE;
import io.choerodon.iam.domain.repository.OrganizationRepository;
import io.choerodon.iam.infra.dataobject.MemberRoleDO;
import io.choerodon.iam.infra.dataobject.OrganizationDO;
import io.choerodon.iam.infra.mapper.MemberRoleMapper;
import io.choerodon.iam.infra.mapper.OrganizationMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wuguokai
 */
@Component
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private OrganizationMapper organizationMapper;
    private MemberRoleMapper memberRoleMapper;

    public OrganizationRepositoryImpl(OrganizationMapper organizationMapper,
                                      MemberRoleMapper memberRoleMapper) {
        this.organizationMapper = organizationMapper;
        this.memberRoleMapper = memberRoleMapper;
    }

    @Override
    public OrganizationE create(OrganizationE organizationE) {
        OrganizationDO organizationDO = ConvertHelper.convert(organizationE, OrganizationDO.class);
        int isInsert = organizationMapper.insertSelective(organizationDO);
        if (isInsert != 1) {
            int count = organizationMapper.selectCount(new OrganizationDO(organizationDO.getName()));
            if (count > 0) {
                throw new CommonException("error.organization.existed");
            }
            throw new CommonException("error.organization.create");
        }
        return ConvertHelper.convert(organizationMapper.selectByPrimaryKey(
                organizationDO.getId()), OrganizationE.class);
    }

    @Override
    public OrganizationE update(OrganizationE organizationE) {
        OrganizationDO organizationDO = ConvertHelper.convert(organizationE, OrganizationDO.class);
        int isUpdate = organizationMapper.updateByPrimaryKeySelective(organizationDO);
        if (isUpdate != 1) {
            throw new CommonException("error.organization.update");
        }
        return ConvertHelper.convert(organizationMapper.selectByPrimaryKey(
                organizationDO.getId()), OrganizationE.class);
    }

    @Override
    public OrganizationDO selectByPrimaryKey(Long organizationId) {
        return organizationMapper.selectByPrimaryKey(organizationId);
    }

    @Override
    public Boolean deleteByKey(Long organizationId) {
        int isDelete = organizationMapper.deleteByPrimaryKey(organizationId);
        if (isDelete != 1) {
            throw new CommonException("error.organization.delete");
        }
        return true;
    }

    @Override
    public Page<OrganizationDO> pagingQuery(OrganizationDO organizationDO, PageRequest pageRequest, String param) {
        return PageHelper.doPageAndSort(pageRequest, () -> organizationMapper.fulltextSearch(organizationDO, param));
    }

    @Override
    public List<OrganizationDO> selectFromMemberRoleByMemberId(Long userId, Boolean includedDisabled) {
        return organizationMapper.selectFromMemberRoleByMemberId(userId, includedDisabled);
    }

    @Override
    public List<OrganizationDO> selectOrgByUserAndPros(Long userId, Boolean includedDisabled) {
        return organizationMapper.selectOrgByUserAndPros(userId, includedDisabled);
    }

    @Override
    public List<OrganizationDO> selectAll() {
        return organizationMapper.selectAll();
    }

    @Override
    public List<OrganizationDO> selectAllOrganizationsWithEnabledProjects() {
        return organizationMapper.selectAllOrganizationsWithEnabledProjects();
    }

    @Override
    public List<OrganizationDO> select(OrganizationDO organizationDO) {
        return organizationMapper.select(organizationDO);
    }

    @Override
    public OrganizationDO selectOne(OrganizationDO organizationDO) {
        return organizationMapper.selectOne(organizationDO);
    }

    @Override
    public Page<OrganizationDO> pagingQueryOrganizationAndRoleById(PageRequest pageRequest, Long id, String params) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int start = page * size;
        PageInfo pageInfo = new PageInfo(page, size);
        int count = memberRoleMapper.selectCountBySourceId(id, "organization");
        List<OrganizationDO> organizationList = organizationMapper.listOrganizationAndRoleById(id, start, size, params);
        return new Page<>(organizationList, pageInfo, count);
    }
}
