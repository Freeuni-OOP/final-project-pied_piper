export const routePaths = {
  home: '/',
  lectures: '/lectures',
  lectureDetail: (id: number | string) => `/lectures/${id}`,
  login: '/login',
  register: '/register',
  verify: '/verify',
  profile: (userId: string) => `/profile/${userId}`,
};
