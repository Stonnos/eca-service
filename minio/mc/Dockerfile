FROM minio/mc
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY . /usr/src/app
# Grant all permissions for the init script to be executable
RUN chmod +x /usr/src/app/create-bucket.sh
ENTRYPOINT exec /usr/src/app/create-bucket.sh