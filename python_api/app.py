#!/usr/bin/env python3
from tornado.web import Application, RequestHandler
from tornado.ioloop import IOLoop
from tornado.concurrent import return_future
from tornado import gen
from cassandra.cluster import Cluster

def main():
    import logging
    db = initdb()
    app = Application([
        (r"/api/(.+)", SearchHandler, dict(db=db)),
    ])
    app.listen(8888)
    logging.info("Listining on port 8888")
    IOLoop.current().start()

class DB(object):
    def __init__(self):
        #self.cluster = Cluster(["cassandra"])
        self.cluster = Cluster()
        self.tables = ["business", "review", "user", "checkin", "tip"]

    @return_future
    def search(self, table, callback=None):
        self.session = self.cluster.connect('users')
        if table not in self.tables:
            return callback(["no matching table"])

        res = self.session.execute("select * from " + table + " limit 10")
        
        if not res:
            return callback([])
        return callback([i for i in res])


def initdb():
    db = DB()
    return db

class SearchHandler(RequestHandler):
    def initialize(self, db):
        self.db = db

    @gen.coroutine
    def get(self, query_string):
        r = yield(self.db.search(query_string))
        self.write(dict(
            size = len(r),
            entries = r
        ))


if __name__ == "__main__":
    main()
