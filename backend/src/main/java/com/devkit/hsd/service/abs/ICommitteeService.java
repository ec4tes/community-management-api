package com.devkit.hsd.service.abs;

import com.devkit.hsd.dto.request.CommitteeRequest;
import com.devkit.hsd.dto.request.UpdateCommitteeRequest;
import com.devkit.hsd.dto.response.CommitteeMemberResponse;
import com.devkit.hsd.dto.response.CommitteeResponse;

import java.util.List;

public interface ICommitteeService {

    public CommitteeResponse createCommittee(CommitteeRequest committeeRequest);

    public List<CommitteeResponse> getAllCommittees();

    public List<CommitteeMemberResponse> getCommitteeMembers(Long id);

    public CommitteeResponse updateCommittee(Long id, UpdateCommitteeRequest updateCommitteeRequest);

    public CommitteeResponse getCommitteeById(Long id);

    public void deleteCommittee(Long id);
}
