import request from '@/utils/request'

export const addSelection = (userId: number, courseId: number) => {
  return request({
    url: '/selection',
    method: 'post',
    params: { userId, courseId }
  })
}

export const deleteSelection = (userId: number, courseId: number) => {
  return request({
    url: '/selection/delete',
    method: 'delete',
    params: { userId, courseId }
  })
}

export const getByUserId = (userId: number) => {
  return request({
    url: `/selection/user/${userId}`,
    method: 'get'
  })
}

export const getByCourseId = (courseId: number) => {
  return request({
    url: `/selection/course/${courseId}`,
    method: 'get'
  })
}
