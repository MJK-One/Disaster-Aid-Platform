// src/location/fetchRegionCode.ts
import { KAKAO_REST_API_KEY } from '@env';

export type Region = {
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

    if (!res.ok) throw new Error(`카카오 API 에러: ${res.status}`);
    const data = await res.json();
    if (data.documents && data.documents.length > 0) {
      const region = data.documents[0];
      return parseSiGu(region.region_1depth_name, region.region_2depth_name);
    }

    return { si: '', gu: null };
  } catch (error) {
    console.error('카카오 지역 변환 오류:', error);
    return { si: '', gu: null };
  }
}

function parseSiGu(region1: string, region2: string): Region {
  let si = '';
  let gu: string | null = null;

  const siMatch = region2.match(/([가-힣]+[시군])/);
  if (siMatch) {
    si = siMatch[1];
  } else {
    si = region1.replace('특별시', '시').replace('광역시', '시');
  }

  const guMatch = region2.match(/([가-힣]+구)$/);
  gu = guMatch ? guMatch[1] : null;

  return { si, gu };
}
