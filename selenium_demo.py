from selenium.webdriver.chrome.options import Options


def build_chrome_options(headless: bool = False) -> Options:
    options = Options()
    if headless:
        options.add_argument("--headless=new")
    options.add_argument("--window-size=1920,1080")
    options.add_argument("--disable-gpu")
    return options


def main() -> None:
    from selenium import webdriver

    driver = webdriver.Chrome(options=build_chrome_options(headless=True))
    try:
        driver.get("https://example.com")
        print(driver.title)
    finally:
        driver.quit()


if __name__ == "__main__":
    main()
