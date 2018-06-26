/*
 * Copyright 2014 James Pether Sörling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *	$Id$
 *  $HeadURL$
*/
package com.hack23.cia.systemintegrationtest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hack23.cia.web.impl.ui.application.action.ViewAction;
import com.hack23.cia.web.impl.ui.application.views.common.pagelinks.api.PageModeMenuCommand;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.CommonsViews;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.UserViews;

/**
 * The Class UserPageVisit.
 */
public final class UserPageVisit extends Assert {

	/** The Constant WAIT_FOR_PAGE_DELAY. */
	private static final int WAIT_FOR_PAGE_DELAY = 15000;

	/** The Constant WAIT_FOR_PAGE_ELEMENT. */
	private static final int WAIT_FOR_PAGE_ELEMENT = 60000;

	/** The screen shot number. */
	private static int screenShotNumber;

	/** The driver. */
	private final WebDriver driver;

	/** The browser. */
	private final String browser;

	/** The action. */
	private final Actions action;

	/** The Constant systemTestTargetUrl. */
	private static final String systemTestTargetUrl;

	static {
		 final String systemTestTargetUrlProperty = System.getProperty("system.test.target.url");
		 if (systemTestTargetUrlProperty != null && systemTestTargetUrlProperty.trim().length() > 0) {
			systemTestTargetUrl = systemTestTargetUrlProperty;
		 } else {
			 systemTestTargetUrl = CitizenIntelligenceAgencyServer.ACCESS_URL;
		 }
	}


	/**
	 * Instantiates a new user page visit.
	 *
	 * @param driver
	 *            the driver
	 * @param browser
	 *            the browser
	 */
	public UserPageVisit(final WebDriver driver, final String browser) {
		super();
		this.driver = driver;
		this.browser = browser;
		action = new Actions(driver);
	}

	/**
	 * Gets the iframes html body as text.
	 *
	 * @return the iframes html body as text
	 */
	public String getIframesHtmlBodyAsText() {
		final StringBuilder stringBuilder = new StringBuilder();
		final List<WebElement> ele = driver.findElements(By.tagName("iframe"));
	    for(final WebElement el : ele){
	    	driver.switchTo().frame(el);

	    	stringBuilder.append(driver.findElement(By.tagName("body")).getText());
	    	driver.switchTo().defaultContent();
	    }
	    return stringBuilder.toString();
	}

	/**
	 * Visit start page.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void visitStartPage() throws Exception {
		driver.get(systemTestTargetUrl);

		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(containsViewAction(ViewAction.VISIT_MAIN_VIEW));

		assertEquals(browser, systemTestTargetUrl,
				driver.getCurrentUrl());
		assertEquals(browser, "Citizen Intelligence Agency", driver.getTitle());
		assertNotNull(browser, driver.getWindowHandle());
		grabScreenshot(driver);

		verifyViewActions(new ViewAction[] {
				ViewAction.VISIT_ADMIN_AGENT_OPERATION_VIEW,
				ViewAction.VISIT_ADMIN_DATA_SUMMARY_VIEW,
				ViewAction.VISIT_POLITICIAN_RANKING_VIEW,
				ViewAction.VISIT_PARTY_RANKING_VIEW,
				ViewAction.VISIT_MINISTRY_RANKING_VIEW,
				ViewAction.VISIT_COMMITTEE_RANKING_VIEW,
				ViewAction.VISIT_COUNTRY_VIEW });
	}


	/**
	 * Visit direct page.
	 *
	 * @param page
	 *            the page
	 * @throws Exception
	 *             the exception
	 */
	public void visitDirectPage(final PageModeMenuCommand page) throws Exception {
		final String url = systemTestTargetUrl  +"#!" + page.getPagePath();
		driver.get(url);

		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(containsViewAction(ViewAction.VISIT_MAIN_VIEW));

		grabScreenshot(driver);

		final String text = getHtmlBodyAsText();
		assertNotNull(text);
		assertFalse("Page contains exception, url:" + url ,text.contains("Exception"));
		assertFalse("Page contains widget exception, url:" + url,text.contains("Widget"));

		wait.until(ExpectedConditions.urlContains(url));
		assertEquals(browser, url, driver.getCurrentUrl());
		assertNotNull(browser, driver.getWindowHandle());

	}

	public ExpectedCondition<Boolean> containsViewAction(final ViewAction value) {
		return new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {
				return getActionsAvailable().contains(value);
			}

			@Override
			public String toString() {
				return String.format("ViewAction \"%s\". ", value);
			}
		};
	}


	/**
	 * Validate page.
	 *
	 * @param page
	 *            the page
	 * @throws Exception
	 *             the exception
	 */
	public void validatePage(final PageModeMenuCommand page) throws Exception {
		final String url = systemTestTargetUrl  +"#!" + page.getPagePath();

		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(containsViewAction(ViewAction.VISIT_MAIN_VIEW));

		final String text = getHtmlBodyAsText();
		assertNotNull(text);
		assertFalse("Page contains exception, url:" + url ,text.contains("Exception"));
		assertFalse("Page contains widget exception, url:" + url,text.contains("Widget"));

		wait.until(ExpectedConditions.urlContains(url));
		assertNotNull(browser, driver.getWindowHandle());

	}


	/**
	 * Gets the html body as text.
	 *
	 * @return the html body as text
	 */
	private String getHtmlBodyAsText() {
		return driver.findElement(By.tagName("body")).getText();
	}

	/**
	 * Check html body contains text.
	 *
	 * @param text
	 *            the text
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	public boolean checkHtmlBodyContainsText(final String text) throws Exception {

		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(containsViewAction(ViewAction.VISIT_MAIN_VIEW));

		wait.until(containsText(text));

		return true;
	}


	public ExpectedCondition<Boolean> containsText(final String value) {
		return new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {
				return getHtmlBodyAsText().contains(value);
			}

			@Override
			public String toString() {
				return String.format("containsText \"%s\". ", value);
			}
		};
	}


	/**
	 * Gets the menu bar.
	 *
	 * @return the menu bar
	 */
	public WebElement getMenuBar() {
		return driver.findElement(By.className("v-menubar"));
	}

	/**
	 * Gets the menu item.
	 *
	 * @param caption
	 *            the caption
	 * @return the menu item
	 */

	public WebElement getMenuItem(final String... caption) {
		return getMenuItem(getMenuBar(), 1,caption);
	}

	/**
	 * Gets the menu item.
	 *
	 * @param element
	 *            the element
	 * @param caption
	 *            the caption
	 * @return the menu item
	 */
	public WebElement getMenuItem(final WebElement element,final String... caption) {

		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(containsMenuItem(element,caption));

		return getMenuItem(element, 1,caption);
	}

	public ExpectedCondition<Boolean> containsMenuItem(final WebElement element,final String... caption) {
		return new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return !ExpectedConditions.stalenessOf(element).apply(driver) && getMenuItem(element, 1,caption) != null;
			}
		};
	}


	/**
	 * Gets the menu item.
	 *
	 * @param element
	 *            the element
	 * @param level
	 *            the level
	 * @param caption
	 *            the caption
	 * @return the menu item
	 */
	private WebElement getMenuItem(final WebElement element,final int level,final String... caption) {
		final List<WebElement> findElements = element.findElements(By.className("v-menubar-menuitem-caption"));
		if (caption.length == level) {
			for (final WebElement webElement : findElements) {
				if (!ExpectedConditions.stalenessOf(element).apply(driver) && webElement.getText().contains(caption[level -1])) {
					return webElement;
				}
			}
		} else {
			for (final WebElement webElement : findElements) {
				if (!ExpectedConditions.stalenessOf(element).apply(driver) && caption[level -1].equals(webElement.getText())) {
					return getMenuItem(webElement, level +1 ,caption);
				}
			}
		}

			final List<WebElement> findElements2 = driver.findElements(By.className("v-menubar-menuitem"));
			if (caption.length == level) {
				for (final WebElement webElement : findElements2) {
						if (!ExpectedConditions.stalenessOf(element).apply(driver) && webElement.getText().contains(caption[level -1])) {
						return webElement;
					}
				}
			}

		return null;
	}


	/**
	 * Gets the buttons.
	 *
	 * @return the buttons
	 */
	public List<WebElement> getButtons() {
		List<WebElement> result = getButtonElements();
		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfAllElements(result)));

		return getButtonElements();
	}

	private List<WebElement> getButtonElements() {
		List<WebElement> result = new ArrayList<>();
		result.addAll(driver.findElements(By.className("v-nativebutton")));
		result.addAll(driver.findElements(By.className("v-button")));
		result.addAll(driver.findElements(By.className("v-button-caption")));
		return result;
	}


	public ExpectedCondition<Boolean> containsButton(final String value) {
		return new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {
				for (final WebElement webElement : getButtons()) {

					if (ExpectedConditions.elementToBeClickable(webElement).apply(driver) != null && value.equalsIgnoreCase(webElement.getText().trim())) {
						return true;
					}
				}

				return false;
			}

			@Override
			public String toString() {
				return String.format("Button \"%s\". ", value);
			}
		};
	}

	/**
	 * Gets the grid rows.
	 *
	 * @return the grid rows
	 */
	public List<WebElement> getGridRows() {
		final WebElement gridBody = driver.findElement(By.className("v-grid-body"));
		return gridBody.findElements(By.className("v-grid-row"));

	}

	/**
	 * Gets the grid headers.
	 *
	 * @return the grid headers
	 */
	public List<WebElement> getGridHeaders() {
		final WebElement gridBody = driver.findElement(By.className("v-grid-header"));

		return gridBody.findElements(By.className("v-grid-row"));

	}


	/**
	 * Visit politician ranking view.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void VisitPoliticianRankingView() throws Exception {
		final WebElement politiciansViewLink = driver.findElement(By
				.id(ViewAction.VISIT_POLITICIAN_RANKING_VIEW.name()));
		performClickAction(politiciansViewLink, WAIT_FOR_PAGE_DELAY);

		assertEquals("https://localhost:28443/#!politicianranking",
				driver.getCurrentUrl());

		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW });

//		final List<String> actionIdsBy = getActionIdsBy(ViewAction.VISIT_POLITICIAN_VIEW);
//		assertTrue(!actionIdsBy.isEmpty());
	}

	/**
	 * Visit politician view.
	 *
	 * @param id
	 *            the id
	 * @throws Exception
	 *             the exception
	 */
	public void VisitPoliticianView(final String id) throws Exception {
		final WebElement politicianViewLink = driver.findElement(By.id(id));
		performClickAction(politicianViewLink, WAIT_FOR_PAGE_DELAY*4);

		//		assertEquals(
		//				"https://localhost:28443/#!politicia/"
		//						+ URLEncoder.encode(id.replace(ViewAction.VisitPoliticianView.name() + "/", "")
		//								.trim(),StandardCharsets.UTF_8), driver.getCurrentUrl());

		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW });
	}


	/**
	 * Visit committee ranking view.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void VisitCommitteeRankingView() throws Exception {
		final WebElement committeeViewLink = driver.findElement(By
				.id(ViewAction.VISIT_COMMITTEE_RANKING_VIEW.name()));
		performClickAction(committeeViewLink, WAIT_FOR_PAGE_DELAY);

		assertEquals("https://localhost:28443/#!committeeranking",
				driver.getCurrentUrl());

		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW });

		final List<String> actionIdsBy = getActionIdsBy(ViewAction.VISIT_COMMITTEE_VIEW);
		assertTrue(!actionIdsBy.isEmpty());
	}

	/**
	 * Visit committee view.
	 *
	 * @param id
	 *            the id
	 * @throws Exception
	 *             the exception
	 */
	public void VisitCommitteeView(final String id) throws Exception {
		final WebElement committeeViewLink = driver.findElement(By.id(id));
		performClickAction(committeeViewLink, WAIT_FOR_PAGE_DELAY);

		//		assertEquals(
		//				"https://localhost:28443/#!committee/"
		//						+ URLEncoder.encode(id.replace(ViewAction.VisitCommitteeView.name() + "/", "")
		//								.trim(),StandardCharsets.UTF_8), driver.getCurrentUrl());

		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW });
	}


	/**
	 * Verify view actions.
	 *
	 * @param viewActions
	 *            the view actions
	 */
	public void verifyViewActions(final ViewAction[] viewActions) {
		final Set<ViewAction> actionsAvailable = getActionsAvailable();
		for (final ViewAction viewAction : viewActions) {
			assertTrue(browser, actionsAvailable.contains(viewAction));

		}
	}

	/**
	 * Visit ministry ranking view.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void VisitMinistryRankingView() throws Exception {
		final WebElement ministryViewLink = driver.findElement(By
				.id(ViewAction.VISIT_MINISTRY_RANKING_VIEW.name()));
		performClickAction(ministryViewLink, WAIT_FOR_PAGE_DELAY);

		assertEquals("https://localhost:28443/#!ministryranking",
				driver.getCurrentUrl());

		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW});

		final List<String> actionIdsBy = getActionIdsBy(ViewAction.VISIT_MINISTRY_VIEW);
		assertTrue(actionIdsBy.size() > 0);
	}

	/**
	 * Visit ministry view.
	 *
	 * @param id
	 *            the id
	 * @throws Exception
	 *             the exception
	 */
	public void VisitMinistryView(final String id) throws Exception {
		final WebElement ministryViewLink = driver.findElement(By.id(id));
		performClickAction(ministryViewLink, WAIT_FOR_PAGE_DELAY);

		//		assertEquals(
		//				"https://localhost:28443/#!ministry/"
		//						+ URLEncoder.encode(id.replace(ViewAction.VisitMinistryView.name() + "/", "")
		//								.trim(),StandardCharsets.UTF_8), driver.getCurrentUrl());

		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW });
	}


	/**
	 * Check notification message.
	 *
	 * @param expectedValue
	 *            the expected value
	 */
	public void checkNotificationMessage(final String expectedValue) {
		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("v-Notification")));
		final WebElement notification = driver.findElement(By.className("v-Notification"));
		assertNotNull(notification);
		assertEquals(expectedValue, notification.getText());

	}

	/**
	 * Visit main view.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void visitMainView() throws Exception {
		final WebElement mainViewLink = driver.findElement(By
				.id(ViewAction.VISIT_MAIN_VIEW.name()));
		performClickAction(mainViewLink, WAIT_FOR_PAGE_DELAY);

		verifyViewActions(new ViewAction[] {
				ViewAction.VISIT_ADMIN_AGENT_OPERATION_VIEW,
				ViewAction.VISIT_ADMIN_DATA_SUMMARY_VIEW,
				ViewAction.VISIT_POLITICIAN_RANKING_VIEW,
				ViewAction.VISIT_PARTY_RANKING_VIEW,
				ViewAction.VISIT_MINISTRY_RANKING_VIEW,
				ViewAction.VISIT_COMMITTEE_RANKING_VIEW,
				ViewAction.VISIT_COUNTRY_VIEW });
	}

	/**
	 * Gets the actions available.
	 *
	 * @return the actions available
	 */
	public Set<ViewAction> getActionsAvailable() {
		final Set<ViewAction> actions = new HashSet<>();

		for (final ViewAction action : ViewAction.values()) {
			if (driver.findElements(By.id(action.name())).size() > 0) {
				actions.add(action);
			}
		}
		return actions;
	}

	/**
	 * Gets the action ids by.
	 *
	 * @param action
	 *            the action
	 * @return the action ids by
	 */
	public List<String> getActionIdsBy(final ViewAction action) {
		final List<String> idList = new ArrayList<>();
		final String xPath = "//*[contains(@id,'" + action.name() + "')]";

		for (final WebElement webElement : driver.findElements(By.xpath(xPath))) {
			idList.add(webElement.getAttribute("id"));

		}

		return idList;
	}




	/**
	 * Visit party view.
	 *
	 * @param id
	 *            the id
	 * @throws Exception
	 *             the exception
	 */
	public void VisitPartyView(final String id) throws Exception {
		final WebElement politicianViewLink = driver.findElement(By.id(id));
		performClickAction(politicianViewLink, WAIT_FOR_PAGE_DELAY);

		//		assertEquals(
		//				"https://localhost:28443/#!party/"
		//						+ URLEncoder.encode(id.replace(ViewAction.VisitPartyView.name() + "/", "")
		//								.trim(),StandardCharsets.UTF_8), driver.getCurrentUrl());

		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW });
	}


	/**
	 * Perform click action.
	 *
	 * @param clickElement
	 *            the click element
	 * @param waitDelay
	 *            the wait delay
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private void performClickAction(final WebElement clickElement, final int waitDelay)
			throws InterruptedException {
		assertNotNull(clickElement);
		final WebDriverWait wait = new WebDriverWait(driver, waitDelay);
		wait.until(ExpectedConditions.elementToBeClickable(clickElement));

		if (browser.contains("htmlunit")) {
			clickElement.click();
		} else {
			action.clickAndHold(clickElement).release().perform();
		}

		wait.until(containsViewAction(ViewAction.VISIT_MAIN_VIEW));

		grabScreenshot(driver);

	}

	/**
	 * Perform click action.
	 *
	 * @param clickElement
	 *            the click element
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void performClickAction(final WebElement clickElement)
			throws InterruptedException {
		performClickAction(clickElement, WAIT_FOR_PAGE_DELAY);
	}


	/**
	 * Visit party ranking view.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void VisitPartyRankingView() throws Exception {
		performClickAction(driver.findElement(By
				.id(ViewAction.VISIT_PARTY_RANKING_VIEW.name())),
				WAIT_FOR_PAGE_DELAY);

		assertEquals("https://localhost:28443/#!partyranking",
				driver.getCurrentUrl());
		verifyViewActions(new ViewAction[] {ViewAction.VISIT_MAIN_VIEW });

		final List<String> actionIdsBy = getActionIdsBy(ViewAction.VISIT_PARTY_VIEW);
		assertTrue(actionIdsBy.size() > 0);

	}

	/**
	 * Register new user.
	 *
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @throws Exception
	 *             the exception
	 */
	public void registerNewUser(final String username,final String password) throws Exception {
		registerNewUserCheckView(username,password,UserViews.USERHOME_VIEW_NAME);
	}

	/**
	 * Register new user check view.
	 *
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param userView
	 *            the user view
	 * @throws Exception
	 *             the exception
	 */
	public void registerNewUserCheckView(final String username,final String password,final String userView) throws Exception {

		setFieldValue("Register.username",username);
		setFieldValue("Register.email",username+"@test.com");
		setFieldValue("Register.country","Sweden");
		setFieldValue("Register.userpassword",password);

		final WebElement registerButton = driver.findElement(By.id("Register"));
		performClickAction(registerButton);

		if (userView != null) {
			final String url = systemTestTargetUrl  +"#!" + userView;
			assertEquals(browser, url,driver.getCurrentUrl());
		}

	}

	public void sendEmailOnEmailPage(final String email,final String subject,final String content) throws Exception {

		setFieldValue("Email.email",email);
		setFieldValue("Email.subject",subject);
		setFieldValue("Email.content",content);

		final WebElement emailButton = driver.findElement(By.id("Email"));
		performClickAction(emailButton);
	}


	/**
	 * Search document.
	 *
	 * @param search
	 *            the search
	 * @throws Exception
	 *             the exception
	 */
	public void searchDocument(final String search) throws Exception {

		setFieldValue("Search.searchExpression",search);

		final WebElement searchButton = driver.findElement(By.id("Search"));
		performClickAction(searchButton);

		final String url = systemTestTargetUrl  +"#!" + UserViews.SEARCH_DOCUMENT_VIEW_NAME;

		assertEquals(browser, url,
				driver.getCurrentUrl());

	}


	/**
	 * Login user.
	 *
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @throws Exception
	 *             the exception
	 */
	public void loginUser(final String username,final String password) throws Exception {
		loginUserCheckView(username,password,UserViews.USERHOME_VIEW_NAME);
	}

	/**
	 * Login user check view.
	 *
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param view
	 *            the view
	 * @throws Exception
	 *             the exception
	 */
	public void loginUserCheckView(final String username,final String password,final String otpCode, final String view) throws Exception {

		setFieldValue("Login.email",username);
		setFieldValue("Login.userpassword",password);

		if (otpCode != null) {
			setFieldValue("Login.otpCode",otpCode);
		}

		final WebElement loginButton =findButton("Login");
		assertNotNull("Expect to find a Login Button",loginButton);

		performClickAction(loginButton);

		final String url = systemTestTargetUrl  +"#!" + view;

		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(ExpectedConditions.urlContains(url));

		assertEquals(browser, url,driver.getCurrentUrl());
	}

	public void loginUserCheckView(final String username,final String password,final String view) throws Exception {
		loginUserCheckView(username, password, null, view);
	}

	/**
	 * Logout user.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void logoutUser() throws Exception {

		final WebElement logoutButton =findButton("Logout");
		assertNotNull("Expect to find a Logout Button",logoutButton);

		final Cookie cookie= driver.manage().getCookieNamed("JSESSIONID");
		final String sessionId = cookie.getValue();


		performClickAction(logoutButton);

		final WebElement body = driver.findElement(By.tagName("body"));
		body.sendKeys(Keys.ESCAPE);

		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(containsViewAction(ViewAction.VISIT_MAIN_VIEW));

		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.tagName("body")));
		driver.navigate().refresh();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.tagName("body")));

		final Cookie newCookie= driver.manage().getCookieNamed("JSESSIONID");

		final String newSessionId = newCookie.getValue();

		assertNotEquals(sessionId,newSessionId);

		final String url = systemTestTargetUrl  +"#!" + CommonsViews.MAIN_VIEW_NAME;

		assertEquals(browser, url,driver.getCurrentUrl());
	}


	/**
	 * Find button.
	 *
	 * @param buttonLabel
	 *            the button label
	 * @return the web element
	 */
	public final WebElement findButton(final String buttonLabel) {
		final WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_PAGE_ELEMENT);
		wait.until(containsButton(buttonLabel));

		for (final WebElement webElement : getButtons()) {
			if (ExpectedConditions.elementToBeClickable(webElement).apply(driver) != null && buttonLabel.equalsIgnoreCase(webElement.getText().trim())) {
				return webElement;
			}
		}
		return null;
	}

	/**
	 * Sets the field value.
	 *
	 * @param id
	 *            the id
	 * @param value
	 *            the value
	 */
	private void setFieldValue(final String id,final String value) {
		final WebElement findElement = driver.findElement(By.id(id));
		findElement.clear();
		findElement.sendKeys(value);
	}

	/**
	 * Update configuration property.
	 *
	 * @param property
	 *            the property
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	public void updateConfigurationProperty(final String property, final String value) throws Exception {
		setFieldValue(property, value);
		final WebElement updateConfigButton = findButton("Update Configuration");
		assertNotNull("Expect to find a Update Config Button",updateConfigButton);

		performClickAction(updateConfigButton);

	}

	/**
	 * Enable google authenticator.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void enableGoogleAuthenticator(final String password) throws Exception {

		setFieldValue("Enable Google Authenticator.userpassword",password);

		final WebElement enableGoogleAuthButton = driver.findElement(By.id("Enable Google Authenticator"));
		assertNotNull("Expect to find a Enable Google Authenticator Button",enableGoogleAuthButton);

		performClickAction(enableGoogleAuthButton);
	}

	public void disableGoogleAuthenticator(final String password) throws Exception {
		setFieldValue("Disable Google Authenticator.userpassword",password);

		final WebElement enableGoogleAuthButton = driver.findElement(By.id("Disable Google Authenticator"));
		assertNotNull("Expect to find a Enable Google Authenticator Button",enableGoogleAuthButton);

		performClickAction(enableGoogleAuthButton);
	}

	/**
	 * Close modal.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void closeModal() throws Exception {
		final WebElement closeModalWindow = driver.findElement(By.className("v-window-closebox"));
		assertNotNull("Expect to find a Window close Box",closeModalWindow);

		performClickAction(closeModalWindow);

	}


	/**
	 * Grab screenshot.
	 *
	 * @param webDriver
	 *            the web driver
	 */
	private static void grabScreenshot(final WebDriver webDriver)
	{
	    final File scrFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
	    try {
	    	screenShotNumber = screenShotNumber +1;
	    	final String screenShotName = webDriver.getCurrentUrl().replace(systemTestTargetUrl, "").replaceAll("#", "-").replaceAll("!", "-").replaceAll("/", "-").replaceAll("--", "-");
	        FileUtils.copyFile(scrFile, new File("target/site/screenshots/Page"+screenShotName+ "-" + screenShotNumber+ ".png"));
	    } catch (final IOException e) {
	        e.printStackTrace();
	    }
	}
}
