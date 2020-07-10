package com.cafe24.dk4750.miniMarket.service;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cafe24.dk4750.miniMarket.mapper.MemberMapper;
import com.cafe24.dk4750.miniMarket.vo.LoginMember;
import com.cafe24.dk4750.miniMarket.vo.Member;
import com.cafe24.dk4750.miniMarket.vo.MemberNickAndPic;
import com.cafe24.dk4750.miniMarket.vo.MemberNickAndPic2;



@Service
@Transactional
public class MemberService {
	@Autowired private MemberMapper memberMapper;
	@Autowired private JavaMailSender javaMailSender;//@Conponent	
	@Value("C:\\Users\\gd\\Documents\\workspace-spring-tool-suite-4-4.6.1.RELEASE\\maven.1593420751967\\miniMarket\\src\\main\\resources\\static\\images\\")
	private String path;
	
	
	//회원탈퇴 
	public String removeMember(LoginMember loginMember) {
		String check = memberMapper.selectMemberPw(loginMember);
		if(check!=null) {
			memberMapper.insertMemberBackUp(loginMember);
			memberMapper.deleteMemberPic(loginMember);
			memberMapper.deleteMember(loginMember);
			memberMapper.deleteMemberTemp(loginMember);
			memberMapper.deleteMemberTempTotal(loginMember); 
			check="탈퇴성공";
		}
		System.out.println(check+"<--check값 2면 탈퇴성공");
		return check;
	}
	
	
	//멤버사진과 닉네임 사진 수정 
	public int modifyMemberNickAndPic(MemberNickAndPic memberNickAndPic) {		
		MultipartFile mf = memberNickAndPic.getProfilePic();
		//확장자 필요		
		String originName= mf.getOriginalFilename();	
		 
		 System.out.println(originName+ "<--originName" ); //user.jpg<--originName
			int lastDot= originName.lastIndexOf("."); // 마지막글자의 . (좌석표.png)
			String extension =originName.substring(lastDot); 
			
			//새로운 이름을 생성: UUID
			String profilePic= memberNickAndPic.getMemberId()+extension; 
			
			//1.디비에서 저장 
			MemberNickAndPic2 memberNickAndPic2 = new MemberNickAndPic2(); 
			memberNickAndPic2.setMemberId(memberNickAndPic.getMemberId());
			memberNickAndPic2.setMemberNickname(memberNickAndPic.getMemberNickname());
			memberNickAndPic2.setProfilePic(profilePic);
			
			System.out.println(memberNickAndPic2+"<-----MemberService.memberNickAndPic1 출력확인");
			System.out.println(profilePic+"<-----profilePic출력확인");
			int row = memberMapper.updateMemberNickname(memberNickAndPic2);
			int row2=memberMapper.updateMemberPic(memberNickAndPic2);
			
			//2.파일저장
			
			File file = new File(this.path+profilePic);
			 
			try {
				mf.transferTo(file);
			} catch (Exception e) {			
				e.printStackTrace();
			}		
		         // Exception 
		         //1.예외처리를 해야만 문법적으로 이상없는 예외 
		         //2.예외처리를 토드에서 구현하지 않아도 아무문제 없는 예외 RuntimeException
		 
		      return row;
	}	
	
	
	// 멤버 수정  닉네임만 수정 
	public int modifyMemberNick(MemberNickAndPic memberNickAndPic) {
		MemberNickAndPic2 memberNick = new MemberNickAndPic2();
		memberNick.setMemberId(memberNickAndPic.getMemberId());
		memberNick.setMemberNickname(memberNickAndPic.getMemberNickname());
		
		int row =memberMapper.updateMemberNickname(memberNick);
		return row;		
	}
	
	
	//멤버사진과 닉네임을 불러와요.^^
		public Map<String, Object> getMemberNickAndPic(LoginMember memberId) {		
			Map<String,Object> map = new HashMap<>(); 
			String memberPic = memberMapper.selectMemberPic(memberId);
			String memberNick = memberMapper.selectMemberNick(memberId);
			
			map.put("memberPic",memberPic);
			map.put("memberNick", memberNick);
			return map;
		}	
	
	//회원 탈퇴 
	public int deleteMember(Member member) {
		return 0;		
	}
		//아이디 이메일 백업하기 고유번호 
		
	//아이디 찾기  
	public String getFindMemberId (Member member) {
		return memberMapper.selectFindMemberId(member);		
	}
	
	//비번찾기
	public int getFindMemberPw(Member member) {
		// 멤버 비밀번호 찾기, 랜덤 비밀번호 만들어줌 
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid);
		
		//랜덤 비번 8글자로 자름 
		String memberPw = uuid.toString().substring(0, 8); 
		member.setMemberPw(memberPw);
		int row = memberMapper.updateMemberFindPw(member);
		if (row == 1) {	
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setTo(member.getMemberEmail()); // 누구에게 보내는지 
			simpleMailMessage.setFrom("hya7835@gmail.com"); // 누가 보내는지
			simpleMailMessage.setSubject("Minimarket 임시 비밀번호 메일");//내용
			simpleMailMessage.setText("변경된 비밀번호는 " + memberPw + " 입니다.");
			javaMailSender.send(simpleMailMessage);
		}
		return row;

	}
	

	//회원 비밀번호 수정 
	public int updateMemberPw(LoginMember loginMember) {
		int row = memberMapper.updateMemberPw(loginMember);
		return row;
	}
	
	//로그인하기 아이디 비번 일치하는지 확인 
	public LoginMember selectLoginMember(LoginMember loginMember) {
		return memberMapper.selectLoginMember(loginMember);		
	}
	
	//나의 정보보기 
	public Member selectMemberOne(LoginMember memberId) {
		return memberMapper.selectMemberOne(memberId);		
	}
	
	//나의 정보 수정(이름,전화번호,주소(동네))
	public int modifyMemberOne(Member member) {		
		return memberMapper.updateMemberOne(member);		
	}
	
	//멤머 정보 입력, 회원가입  
	public int addMember(Member member) {
		//랜덤 비번 만들기 
		UUID uuid = UUID.randomUUID();
		String memberPw= uuid.toString().substring(0,4);//0번째부터 4번쨰까지 
		member.setMemberPw(memberPw);
		System.out.println(memberPw +"<--랜덤memberPw  확인");
		System.out.println(member.getMemberPw()+"<-랜덤 비번getMemberPw 잘 들어갔나 확인");
		// 유니크 넘버 만들어 줘야됨 	
		String memberUniqueNo = memberMapper.selectMemberUniqueNo();
		System.out.println(memberUniqueNo+"<--유니크넘버임");
		member.setMemberUniqueNo(memberUniqueNo);
		
		int row = memberMapper.insertMember(member);
	
		if(row==1) {
			//회원가입 완료되면 사진 넣기 
			memberMapper.insertMemberPic(member.getMemberId());				
			System.out.println(memberPw+"<--임시 비밀번호 들어갔는지 확인 memberPw");
			//온도넣기
			memberMapper.insertMemberTemp(member.getMemberUniqueNo());
			
			//회원가입 완료되면 생성된 임시 비번 메일로 보내주기 
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setTo(member.getMemberEmail()); // 누구에게 보내는지 
			simpleMailMessage.setFrom("hya7835@gmail.com"); // 누가 보내는지
			simpleMailMessage.setSubject("Minimarket 임시 비밀번호 메일");//내용
			simpleMailMessage.setText("지정된 임시 비밀번호:"+ memberPw+"입니다" +"로그인 후 비밀번호를 변경해주세요");
			javaMailSender.send(simpleMailMessage);
		}
		return row;		
	}
}
