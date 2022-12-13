FROM alpine:latest

LABEL MAINTAINER="Raj Jagtap <rjagtap@purdue.edu>"

RUN apk --update add nginx && \
    ln -sf /dev/stdout /var/log/nginx/access.log && \
    ln -sf /dev/stderr /var/log/nginx/error.log && \
    mkdir /etc/nginx/sites-enabled/ && \
    mkdir -p /run/nginx && \
    rm -rf /etc/nginx/conf.d/default.conf && \
    rm -rf /var/cache/apk/*

COPY conf.d/app.conf /etc/nginx/conf.d/app.conf
COPY conf.d/app.conf /etc/nginx/nginx.conf

EXPOSE 1080
CMD ["nginx", "-g", "daemon off;"]