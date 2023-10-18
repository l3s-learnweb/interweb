class InterwebError(Exception):
    def __init__(
            self,
            http_status=None,
            message=None,
            body=None,
            headers=None,
            code=None,
    ):
        super(InterwebError, self).__init__(message)

        self.http_status = http_status
        self._message = message
        self.body = body
        self.headers = headers or {}
        self.code = code


class Timeout(InterwebError):
    pass


class Unauthorized(InterwebError):
    pass


class InvalidRequestError(InterwebError):
    pass
