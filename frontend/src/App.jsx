import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./components/auth/LoginPage.jsx";
import Layout from "./components/layout/Layout.jsx";
import ProjectListPage from "./components/projectList/ProjectListPage.jsx";
import ProjectDetailPage from "./components/projectDetail/ProjectDetailPage.jsx";
import ApplyPage from "./components/apply/ApplyPage.jsx";
import DeveloperMyPage from "./components/developerMyPage/DeveloperMyPage.jsx";
import ClientProjectFormPage from "./components/clientMyPage/ClientProjectFormPage.jsx";
import ClientMyPage from "./components/clientMyPage/ClientMyPage.jsx";
import ClientProfilePanel from "./components/clientMyPage/ClientProfilePanel.jsx";
import ClientProjectDetailPage from "./components/clientMyPage/ClientProjectDetailPage.jsx";
import { RequireAuth, RequireRole } from "./components/auth/RouteGuard.jsx";
import PartnersPage from "./components/partners/PartnersPage.jsx";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />

      <Route element={<RequireAuth />}>
        <Route element={<Layout />}>
          <Route path="/projects" element={<ProjectListPage />} />
          <Route path="/partners" element={<PartnersPage />} />
          <Route path="/projects/:projectId" element={<ProjectDetailPage />} />

          <Route
            path="/projects/:projectId/apply"
            element={
              <RequireRole role="DEVELOPER">
                <ApplyPage />
              </RequireRole>
            }
          />

          <Route
            path="/developer/mypage"
            element={
              <RequireRole role="DEVELOPER">
                <DeveloperMyPage />
              </RequireRole>
            }
          />

          <Route
            path="/client/new-project"
            element={<Navigate to="/client-project-form" replace />}
          />

          <Route
            path="/client-project-form"
            element={
              <RequireRole role="CLIENT">
                <ClientProjectFormPage />
              </RequireRole>
            }
          />

          <Route
            path="/client/mypage"
            element={<Navigate to="/client-projects" replace />}
          />

          <Route
            path="/client-mypage"
            element={
              <RequireRole role="CLIENT">
                <ClientProfilePanel />
              </RequireRole>
            }
          />

          <Route
            path="/client-projects"
            element={
              <RequireRole role="CLIENT">
                <ClientMyPage />
              </RequireRole>
            }
          />

          <Route
            path="/client/projects/:projectId"
            element={
              <RequireRole role="CLIENT">
                <ClientProjectDetailPage />
              </RequireRole>
            }
          />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
