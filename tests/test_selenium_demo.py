from selenium_demo import build_chrome_options


def test_build_chrome_options_enables_headless_mode():
    options = build_chrome_options(headless=True)
    assert "--headless=new" in options.arguments
