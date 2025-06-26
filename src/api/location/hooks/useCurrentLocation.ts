// src/hooks/useCurrentLocation.ts
import { useState, useEffect } from 'react';
import { getCurrentLocation } from '../utils/location';
import fetchRegionCode from '../utils/fetchRegionCode';

export function useCurrentLocation() {
  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);
  const [si, setSi] = useState('');
  const [gu, setGu] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      const coords = await getCurrentLocation();
      if (coords) {
        const { latitude, longitude } = coords;
        setLatitude(latitude);
        setLongitude(longitude);

        const region = await fetchRegionCode(latitude, longitude);
        setSi(region.si);
        setGu(region.gu);
      }
      setLoading(false);
    })();
  }, []);

  return { latitude, longitude, si, gu, loading };
}
