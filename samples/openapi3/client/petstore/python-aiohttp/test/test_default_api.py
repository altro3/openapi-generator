# coding: utf-8

"""
    OpenAPI Petstore

    This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from petstore_api.api.default_api import DefaultApi


class TestDefaultApi(unittest.IsolatedAsyncioTestCase):
    """DefaultApi unit test stubs"""

    async def asyncSetUp(self) -> None:
        self.api = DefaultApi()

    async def asyncTearDown(self) -> None:
        await self.api.api_client.close()

    async def test_foo_get(self) -> None:
        """Test case for foo_get

        """
        pass


if __name__ == '__main__':
    unittest.main()
