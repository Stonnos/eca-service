/usr/bin/mc alias set minio $S3_MINIO_URL $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD
/usr/bin/mc mb minio/$BUCKET_NAME --ignore-existing
/usr/bin/mc policy download minio/$BUCKET_NAME
exit 0