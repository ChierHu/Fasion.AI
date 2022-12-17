FROM node:14 AS builder

ENV GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no"
# RUN mkdir -p -m 0600 ~/.ssh && ssh-keyscan 120.92.82.131 >> ~/.ssh/known_hosts
RUN --mount=type=ssh git clone ssh://git@120.92.82.131:8022/fasion/fasion-web.git

WORKDIR /fasion-web

ARG git_ref=not_set

RUN git checkout ${git_ref}

RUN yarn config set registry https://registry.npm.taobao.org/
RUN yarn install && yarn build

FROM caddy:2

COPY --from=builder /fasion-web/dist /srv