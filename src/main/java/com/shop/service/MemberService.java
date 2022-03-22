package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@Transactional  //작업중 에러 생길 시 쿼리 롤백
@RequiredArgsConstructor    //final이나 @NonNull이 붙은 필드에 생성자를 생성해 줍니다. 빈에 생성자가 1개ㅔ이고 생성자의 파라미터 타입이 빈으로 등록 가능하면 @Autowired 없이 의존선 주입 가능
public class MemberService implements UserDetailsService {  // UserDetailsService 구현

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  // UserDetailsService 인터페이스의 loadUseByUsername() 메소드를 오버라이딩. 로그인할 유저의 eamil을 파라미터로 전달

        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder()   //UserDetail을 구현하고 있는 User 객체를 반환, User 객체를 생성하기 위해 생성자로 회원의 이메일, 비밀번호, role을 파라미터로 넘겨줌
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}