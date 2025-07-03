// 역할 타입
export type UserRole = 'IND' | 'NGO' | 'GOV';

// 로그인 방식 타입
export type LoginType = 'LOCAL' | 'GOOGLE' | 'KAKAO';

// 회원가입 요청 DTO
export interface SignUpRequestDto {
  email: string;
  password: string;
  nickname: string;
  phoneNumber: string;
  si: string; // ✅ 지역 필드 추가
  userRole: UserRole;
  loginType: LoginType; // 일반적으로 'LOCAL', 소셜은 서버에서 자동 설정
  provider?: string | null; // 소셜 로그인 시 'kakao', 'google'
  organizationName?: string | null; // NGO, GOV일 경우 사용 가능
}

// 로그인 요청 DTO
export interface LoginRequestDto {
  email: string;
  password?: string; // ✅ 소셜 로그인 시 없어도 됨
  loginType: LoginType; // 'LOCAL' | 'KAKAO' | 'GOOGLE'
  provider?: string | null; // ✅ 소셜 로그인 시 명시 (e.g., 'kakao')
}
