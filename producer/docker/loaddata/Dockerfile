FROM alpine:3.7

RUN apk update && apk add mysql-client
ADD myapp.sql /root/

CMD ["sh", "-c" , "while sleep 3600; do :; done"]
