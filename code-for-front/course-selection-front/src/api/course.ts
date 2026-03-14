import request from '@/utils/request'

export const listCourses = (params: any) => {
  return request({
    url: '/course',
    method: 'get',
    params
  })
}

export const getCourseById = (id: number) => {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

export const addCourse = (data: any) => {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

export const updateCourse = (data: any) => {
  return request({
    url: '/course',
    method: 'put',
    data
  })
}

export const deleteCourse = (ids: string) => {
  return request({
    url: `/course/${ids}`,
    method: 'delete'
  })
}
