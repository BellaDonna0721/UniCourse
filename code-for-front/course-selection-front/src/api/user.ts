import request from '@/utils/request'

export const login = (data: any) => {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export const listUsers = (params: any) => {
  return request({
    url: '/user',
    method: 'get',
    params
  })
}

export const getUserById = (id: number) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export const addUser = (data: any) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export const updateUser = (data: any) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export const deleteUser = (ids: string) => {
  return request({
    url: `/user/${ids}`,
    method: 'delete'
  })
}
