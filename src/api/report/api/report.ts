import axiosInstance from '../../global/api/axiosInstance.ts';
import { ApiResponse, ReportResponse } from '../types/api.ts';

export const getReports = async () => {
    const res = await axiosInstance.get<ApiResponse<ReportResponse[]>>('/reports');
    return res.data.payload;
};

export const createReport = async (payload: {
    disasterType: string;
    description: string;
    imageUrl?: string;
    videoUrl?: string;
    si: string;
    gu: string;
    latitude: number;
    longitude: number;
}) => {
    const res = await axiosInstance.post<ApiResponse<ReportResponse>>('/reports', payload);
    console.log('📥 신고 응답:', res.data); // 디버깅용
    return res.data.payload;
};

export const updateReportStatus = async (id: number, newStatus: string) => {
    await axiosInstance.patch(`/reports/${id}/status?newStatus=${newStatus}`);

};