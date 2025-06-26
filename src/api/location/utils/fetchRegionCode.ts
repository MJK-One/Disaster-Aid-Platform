
import { KAKAO_REST_API_KEY } from '@env';

type Region = {
  si: string;
  gu: string | null;
};

export default async function fetchRegionCode(latitude: number, longitude: number): Promise<Region> {
  try {
    const res = await fetch(
      `https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=${longitude}&y=${latitude}`,
      {
        method: 'GET',
        headers: {
          Authorization: `KakaoAK ${KAKAO_REST_API_KEY}`,
        },
      }
    );

    if (!res.ok) {
      throw new Error(`카카오 API 에러: ${res.status}`);
    }

    const data = await res.json();

    if (data.documents && data.documents.length > 0) {
      const region = data.documents[0];
      const parsed = parseSiGu(region.region_1depth_name, region.region_2depth_name);
      return parsed; // ✅ 타입 안전
    }

    return { si: '', gu: null }; // 기본값 반환
  } catch (error) {
    console.error('카카오 좌표→지역변환 오류:', error);
    return { si: '', gu: null };
  }
}

function parseSiGu(region_1depth_name: string, region_2depth_name: string): Region {
  let si = region_1depth_name;
   let gu: string | null = region_2depth_name;

  const provinces = [
    '경기도',
    '충청북도',
    '충청남도',
    '경상북도',
    '경상남도',
    '전라북도',
    '전라남도',
    '강원도',
    '제주특별자치도',
  ];

  for (const province of provinces) {
    if (si.startsWith(province)) {
      const parts = si.split(' ');
      if (parts.length > 1) {
        si = parts[1];
      }
      break;
    }
  }

  if (!gu.includes('구')) {
    gu = null;
  }

  return { si, gu };
}
