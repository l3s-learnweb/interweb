import interweb


def test_suggest():
    result = interweb.suggest(query="test")
    assert len(result['results']) >= 2


def test_suggest_bing():
    result = interweb.suggest(query="test", services=["bing"])
    assert len(result['results']) == 1
    assert result['results'][0]['service'] == "Bing"
