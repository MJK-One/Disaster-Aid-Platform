export type UserRole = 'IND' | 'NGO' | 'GOV';
export type LoginType = 'LOCAL' | 'GOOGLE' | 'KAKAO';

export interface SignUpRequestDto {
  email: string;
  password: string;
  nickname: string;
  phoneNumber: string;
  userRole: 'IND' | 'NGO'| 'GOV';
  loginType: 'LOCAL'; // OAuth2는 버튼 클릭 시 자동 설정
  provider?: string | null;
  organizationName?: string | null;
}

export interface LoginRequestDto {
  email: string;
  password: string;
  loginType: 'LOCAL';
}