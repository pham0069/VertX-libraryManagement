# sudo docker build -t lib-postgres .
# sudo docker run -p 5430:5432 --name psql -d lib-postgres
# sudo docker exec -it psql bash
# psql -U diep -d library
# \dt # to check if tables are initialized as expected

FROM postgres

ENV POSTGRES_PASSWORD=diep
ENV POSTGRES_USER=diep
# need to specify DB here, or the script will initialize the tables to database named diep (same as user)
ENV POSTGRES_DB=library

COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 5432

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["postgres"]
