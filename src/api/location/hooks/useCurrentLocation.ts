import { useState, useEffect } from 'react';
import { NativeEventEmitter, NativeModules, Platform } from 'react-native';
import fetchRegionCode from '../utils/fetchRegionCode';

const { IntentLauncher, LocationCache } = NativeModules;  // LocationCache는 캐시 위치용 네이티브 모듈 가정

export function useCurrentLocation() {
  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);
  const [si, setSi] = useState('');
  const [gu, setGu] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    let timeoutId: NodeJS.Timeout | null = null;

    async function init() {
      if (Platform.OS === 'android') {
        const eventEmitter = new NativeEventEmitter(IntentLauncher);

        // 위치 업데이트 이벤트 수신
        const subscription = eventEmitter.addListener('onLocationUpdate', async (event) => {
          if (!isMounted) return;

          const { latitude, longitude } = event;
          console.log('[Location Update] latitude:', latitude, 'longitude:', longitude);

          setLatitude(latitude);
          setLongitude(longitude);

          const region = await fetchRegionCode(latitude, longitude);
          setSi(region.si);
          setGu(region.gu);

          setLoading(false);

          // 위치 받았으니 타임아웃 취소
          if (timeoutId) {
            clearTimeout(timeoutId);
            timeoutId = null;
          }
        });

        // 1) 위치 캐시에서 초기 위치 읽기 시도
        async function fetchCachedLocation() {
          try {
            const cached = await LocationCache.getLastLocation();
            console.log('[Cached Location]', cached);

            if (cached?.latitude && cached?.longitude) {
              if (!isMounted) return;

              setLatitude(cached.latitude);
              setLongitude(cached.longitude);

              const region = await fetchRegionCode(cached.latitude, cached.longitude);
              setSi(region.si);
              setGu(region.gu);

              setLoading(false);

              // 캐시 위치가 있으면 타임아웃 취소
              if (timeoutId) {
                clearTimeout(timeoutId);
                timeoutId = null;
              }
            }
          } catch (e) {
            console.warn('위치 캐시 불러오기 실패:', e);
          }
        }

        // 2) 타임아웃: 10초 후에도 위치 못 받으면 강제로 loading 종료
        timeoutId = setTimeout(() => {
          if (isMounted && loading) {
            console.warn('위치 수신 타임아웃으로 loading 종료');
            setLoading(false);
          }
        }, 10000);

        fetchCachedLocation();

        // 서비스 시작 (한번만 호출)
        IntentLauncher.startService('com.disasteraidplatform.RecordingService', 'START');

        return () => {
          isMounted = false;
          subscription.remove();
          IntentLauncher.startService('com.disasteraidplatform.RecordingService', 'STOP');

          if (timeoutId) {
            clearTimeout(timeoutId);
            timeoutId = null;
          }
        };
      } else {
        setLoading(false); // Android 아닌 경우 바로 종료
      }
    }
    init();

    return () => {
      isMounted = false;
    };
  }, []);

  return { latitude, longitude, si, gu, loading };
}
