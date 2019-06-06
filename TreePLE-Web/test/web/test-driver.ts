// Project 6 Proprietary Selenium Testing Framework
// (c) Jamie Day, 2018

import { Builder, By, Key, until, WebDriver, WebElement } from 'selenium-webdriver';
import assert = require('power-assert');

const DEFAULT_WAIT_TIMEOUT = 3000;

// Currently only used in dev
const frontendUrl = 'http://localhost:8087';

(async function runAllTests() {
  const driver = await new Builder()
    .forBrowser('firefox')
    .build();

  try {
    await driver.get(frontendUrl);
    const tests = generateTests(driver, new Util(driver));

    const testsFailed: { [index: string] : boolean } = {};
    const testsCompleted: { [index: string] : boolean } = {};
    const testsSkipped: { [index: string] : boolean } = {};

    /**
     * @param test
     * @returns completed (boolean)
     */
    async function runTest(test: Test) {
      try {
        // Only run each test once
        if (testsCompleted.hasOwnProperty(test.name)) return true;
        if (testsFailed.hasOwnProperty(test.name)) return false;
        
        // Ensure dependencies have been run
        for (let dep of test.dependencies) {
          // If dependency isn't successful, skip this test
          if (!await runTest(tests[dep])) {
            testsSkipped[test.name] = true;
            console.log(`Dependencies failed. Skipping ${test.name}...`);
            return false;
          }
        }

        console.log(`Running ${test.name}...`);
        await test.run();

        testsCompleted[test.name] = true;
        return true;
      } catch (e) {
        console.error('---------------------------');
        console.error(`Test failed: ${test.name}`);
        console.error('---------------------------');
        console.error(e.stack);
        testsFailed[test.name] = true;
        return false;
      }
    }

    // Bootstrap - run tests
    for (let i in tests) {
      if (tests.hasOwnProperty(i)) {
        await runTest(tests[i]);
      }
    }

    const numFailed = Object.keys(testsFailed).length;
    const numSkipped = Object.keys(testsSkipped).length;

    console.log(`\nDone! ${numFailed == 0 ? 'All tests passed.' : `${numFailed} test${numFailed == 1 ? '' : 's'} failed${numSkipped ? `, ${numSkipped} skipped` : ''}.`}`);
  } catch (e) {
    console.error("Unexpected bootstrapping error");
  } finally {
    await driver.quit();
  }
})();

interface Test {
  name: string;
  run(): Promise<void>;
  dependencies: string[];
}

class Util {
  driver: WebDriver;
  
  constructor(driver: WebDriver) {
    this.driver = driver;
  }

  async sendInputs(cssMap: {[index:string] : string | number}) {
    for (let i in cssMap) {
      if (cssMap.hasOwnProperty(i)) {
        const el = await this.driver.findElement(By.css(i));
        await el.clear();
        await el.sendKeys(cssMap[i]);
      }
    }
  }
  async selectOption(selectSelector: string, optionValue: string) {
    const select = await this.driver.findElement(By.css(selectSelector));
    const option = await select.findElement(By.css(`option[value="${optionValue}"]`));
    await option.click();
  }
}

const generateTests: (driver: WebDriver, util: Util) => { [index:string] : Test } = (driver, util) => ({
  'authentication': {
    name: 'authentication',
    dependencies: [],
    run: async () => {
      const username = 'Bob' + Math.floor(Math.random()*10000000);
      const password = 'bobspass';

      // Try signup
      await util.sendInputs({
        '#login-username': username,
        '#login-password': password
      });
      await (await driver.findElement(By.id('button-signup'))).click();
      
      // Logged in success, logout
      await driver.wait(until.elementLocated(By.id('button-logout')), DEFAULT_WAIT_TIMEOUT);
      await (await driver.findElement(By.id('button-logout'))).click();

      await driver.wait(until.elementLocated(By.id('button-login')), DEFAULT_WAIT_TIMEOUT);

      // Try login invalid password
      await util.sendInputs({
        '#login-username': username,
        '#login-password': 'invalid_password'
      });
      await (await driver.findElement(By.id('button-login'))).click();

      // Ensure there was an error
      await driver.wait(until.elementLocated(By.className('error')), DEFAULT_WAIT_TIMEOUT, 'Invalid login info resulted in error');
      const errElement = await driver.findElement(By.className('error'));
      assert.equal(await errElement.getText(), 'Your login information is incorrect.', 'Correct login error message shown');

      // Try login success
      await util.sendInputs({
        '#login-username': username,
        '#login-password': password
      });
      await (await driver.findElement(By.id('button-login'))).click();

      // Ensure we've reached the homepage
      await driver.wait(until.elementLocated(By.id('treeple')), DEFAULT_WAIT_TIMEOUT);
    }
  },
  'addTree': {
    name: 'addTree',
    dependencies: ["authentication"],
    run: async () => {
      await driver.navigate().to(`${frontendUrl}/#/addtree`);

      interface Tree {
        latitude: number;
        longitude: number;
        height: number;
        canopy: number;
        species: string;
        landType: string;
        municipality: string;
      }

      // Randomness used just in case so we don't test the same values every time
      const newTree = {
        latitude: parseFloat((-90 + Math.random()*180).toFixed(1)),
        longitude: parseFloat((-180 + Math.random()*360).toFixed(1)),
        height: Math.floor(Math.random()*100)+1,
        canopy: Math.floor(Math.random()*100)+1,
        species: ['Oak', 'Birch', 'Maple', 'Willow'][Math.floor(Math.random()*4)],
        landType: ['Park', 'Institutional', 'Municipal'][Math.floor(Math.random()*3)],
        municipality: 'Anjou'
      };

      const toInput = (tree: Tree) => ({
        '#input-latitude': tree.latitude,
        '#input-longitude': tree.longitude,
        '#input-height': tree.height,
        '#input-canopy': tree.canopy,
        '#input-species': tree.species,
      });

      await util.sendInputs(toInput(newTree));

      await util.selectOption('#select-municipality', newTree.municipality);
      await util.selectOption('#select-landType', newTree.landType);
      await (await driver.findElement(By.id('button-plant'))).click()

      const successElement = await driver.findElement(By.css('.message-container.success'));
      assert.equal(await successElement.getText(), 'Tree created successfully.', 'Tree planted successfully');
      await driver.navigate().to(`${frontendUrl}/#/`);

      await driver.wait(until.elementLocated(By.className('tree-table')), DEFAULT_WAIT_TIMEOUT);
      await driver.sleep(500);
      
      // Expand list if applicable
      const exp = await driver.findElements(By.id('expander'));
      let lastTree = null;
      if (exp.length == 1) {
        await exp[0].click();
        lastTree = await driver.findElement(By.css('.tree-table > tbody > tr:nth-last-child(2)'));
      } else {
        lastTree = await driver.findElement(By.css('.tree-table > tbody > tr:last-child'));
      }

      // Ensure tree planted shows in list
      const dataRows = await lastTree.findElements(By.tagName('td'));

      const listTreeMatch: ((tree: Tree) => string)[] = [
        () => 'Planted',
        () => 'Healthy',
        () => 'None',
        tree => tree.species,
        tree => tree.canopy.toString(),
        tree => tree.height.toString(),
        tree => tree.landType,
        tree => tree.latitude.toString(),
        tree => tree.longitude.toString(),
      ];

      await Promise.all(dataRows.map(async (el: WebElement, i) => {
        if (listTreeMatch[i]) {
          assert.equal(await el.getText(), listTreeMatch[i](newTree), `New tree matches on list [column ${i}]`);
        }
      }));
    }
  }
});