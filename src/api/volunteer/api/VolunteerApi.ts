import axiosInstance from '../../global/api/axiosInstance';
import type { CreatePostRequest } from '../types/Post';

export const volunteerApi = {
  createPost: (data: CreatePostRequest) =>
    axiosInstance.post('/post', data),
};