export interface VolunteerPost {
  id: number;
  title: string;
  organizationName: string;
  createdAt: string;
  status: '모집' | '완료';
  participants: number;
  capacity: number;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  category: 'RECRUITMENT'; // 무조건 RECRUITMENT로 고정
  totalCapacity: number;
  teamSize: number;
  location: {
    placeName: string;
    latitude: number;
    longitude: number;
  };
  attendancePolicy: {
    checkinStart: string; // ISO 형식 e.g., "2025-06-20T09:00:00"
    checkinEnd: string;
    allowedRadiusM: number;
    minStayMinutes: number;
  };
}