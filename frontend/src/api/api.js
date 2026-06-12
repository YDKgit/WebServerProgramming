import { mockApi } from './mockApi';
import { getMockPartners } from './partnersMock';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true';

async function request(path, options = {}) {
  if (USE_MOCK) {
    return mockApi(path, options);
  }

  const headers = options.body instanceof FormData
    ? options.headers || {}
    : { 'Content-Type': 'application/json', ...(options.headers || {}) };

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
    credentials: 'include',
  });

  let body = null;
  try {
    body = await response.json();
  } catch {
    body = null;
  }

  if (!response.ok || body?.success === false) {
    const message =
      body?.message ||
      body?.error ||
      body?.data?.message ||
      body?.data ||
      '요청 처리 중 오류가 발생했습니다.';
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  return body?.data ?? body;
}

function toQueryString(params) {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '' && value !== 'ALL') {
      searchParams.append(key, value);
    }
  });

  const query = searchParams.toString();
  return query ? `?${query}` : '';
}

export const authApi = {
  login(payload) {
    return request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  logout() {
    return request('/auth/logout', {
      method: 'POST',
    });
  },
};

export const projectApi = {
  getProjects({
    keyword,
    type,
    employmentType,
    participation,
    region,
    status,
    sort,
    page,
    size,
  }) {
    return request(`/projects${toQueryString({
      page,
      size,
      sort,
      keyword,
      type,
      employmentType,
      participation,
      region,
      status,
    })}`);
  },

  getProject(projectId) {
    return request(`/projects/${projectId}`);
  },

  createProject(payload) {
    return request('/projects', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  getClientProjects() {
    return request('/projects/client/my');
  },

  getClientProjectDetail(projectId) {
    return request(`/projects/${projectId}`);
  },

  getApplicants(projectId, { page = 0, size = 2 } = {}) {
    return request(`/projects/${projectId}/applicants${toQueryString({ page, size })}`);
  },

  updateProject(projectId, payload) {
    return request(`/projects/${projectId}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  },
};

export const developerApi = {
  getProfile() {
    return request('/member/profile');
  },

  updateProfile(payload) {
    return request('/member/profile', {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  },

  uploadProfileImage(file) {
    const formData = new FormData();
    formData.append('image', file);

    return request('/member/profile/image', {
      method: 'POST',
      body: formData,
    });
  },

  applyProject(projectId, payload) {
    return request('/applications', {
      method: 'POST',
      body: JSON.stringify({
        projectId: Number(projectId),
        ...payload,
      }),
    });
  },

  getAppliedProjects() {
    return request('/applications/my');
  },

  getApplication(applicationId) {
    return request(`/applications/${applicationId}`);
  },
};

export const applicationApi = {
  getApplication(applicationId) {
    return request(`/applications/${applicationId}`);
  },

  acceptApplication(applicationId) {
    return request(`/applications/${applicationId}/accept`, {
      method: 'PATCH',
    });
  },
};

export const partnersApi = {
  getPartners(params = {}) {
    return getMockPartners(params);
  },
};
