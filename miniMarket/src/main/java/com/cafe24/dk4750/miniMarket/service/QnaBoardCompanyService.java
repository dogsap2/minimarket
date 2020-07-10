package com.cafe24.dk4750.miniMarket.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cafe24.dk4750.miniMarket.mapper.QnaBoardCompanyMapper;
import com.cafe24.dk4750.miniMarket.vo.QnaBoardCompany;

@Service
@Transactional
public class QnaBoardCompanyService {
	@Autowired
	private QnaBoardCompanyMapper qnaBoardCompanyMapper;
	// QnA리스트 출력
	public Map<String, Object> getQnaBoardCompanyList(int currentPage) {
		int rowPerPage = 10;
		int beginRow = (currentPage-1)*rowPerPage;
		Map<String, Object> map = new HashMap<>();
		map.put("beginRow", beginRow);
		map.put("rowPerPage", rowPerPage);
		// lastPage
		int totalRow = qnaBoardCompanyMapper.getTotalRow();
		int lastPage = totalRow/rowPerPage;
		if(totalRow%rowPerPage != 0) {
			lastPage += 1;
		}
		// list와 lastPage를 Map에 담는다
		List<QnaBoardCompany> qnaBoardCompanyBoardList = qnaBoardCompanyMapper.selectCompanyBoardQnAList(map);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("qnaBoardCompanyBoardList", qnaBoardCompanyBoardList);
		map2.put("lastPage", lastPage);
		
		return map2;
	}
}
