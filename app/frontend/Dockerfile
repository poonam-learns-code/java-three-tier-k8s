FROM nginx:alpine

RUN rm -f /etc/nginx/conf.d/default.conf
COPY index.html /usr/share/nginx/html/index.html

RUN printf '%s\n' \
  'server {' \
  '    listen 80;' \
  '    server_name _;' \
  '    root /usr/share/nginx/html;' \
  '    index index.html;' \
  '    location / {' \
  '        try_files $uri $uri/ /index.html;' \
  '    }' \
  '    location /api/ {' \
  '        proxy_pass http://backend:8080/api/;' \
  '        proxy_set_header Host $host;' \
  '        proxy_set_header X-Real-IP $remote_addr;' \
  '    }' \
  '}' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]