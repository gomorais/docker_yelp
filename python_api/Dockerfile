FROM python:3

ADD app.py /api/
ADD requirements.txt /api/

WORKDIR /api

RUN pip install -r requirements.txt

EXPOSE 8888
