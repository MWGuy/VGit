FROM node as build-frontend
WORKDIR /frontend
COPY ./frontend/package.json /frontend/package.json
RUN npm install --legacy-peer-deps
COPY ./frontend /frontend
RUN npm run build
FROM gradle:jdk11 as full-build
ENV GIT_BASE_DIRECTORY=/data
COPY --from=build-frontend /backend/src/main/resources/static /app/backend/src/main/resources/static
COPY ./git-cli /app/git-cli
COPY ./settings.gradle /app/settings.gradle
COPY ./backend /app/backend
WORKDIR /app
RUN gradle backend:bootJar
CMD [ "java", "-jar", "backend/build/libs/backend.jar" ]
