package hu.topclouders.bemszobor.test;

import hu.topclouders.bemszobor.dao.IActionRepository;
import hu.topclouders.bemszobor.dao.ILocationRepository;
import hu.topclouders.bemszobor.dao.IProtestRepository;
import hu.topclouders.bemszobor.domain.Action;
import hu.topclouders.bemszobor.domain.Demonstration;
import hu.topclouders.bemszobor.domain.Location;
import hu.topclouders.bemszobor.domain.Visitor;
import hu.topclouders.bemszobor.service.ProtestService;
import hu.topclouders.bemszobor.service.VisitorService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = "/service-context.xml")
public class MongoDBTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private VisitorService registrationService;

	@Autowired
	private IProtestRepository protestRepository;

	@Autowired
	private ILocationRepository locationDao;

	@Autowired
	private IActionRepository actionRepository;

	@Autowired
	private ProtestService protestService;

	private List<Demonstration> protests = new ArrayList<Demonstration>();

	@BeforeClass
	public void beforeClass() {

		Map<Demonstration, Long> activeDemonstrations = protestService
				.getActiveDemonstrators();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 3);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -4);
		for (int i = 0; i < 10; i++) {

			protests.add(createProtest("protest" + i, cal.getTime()));

			cal.add(Calendar.DATE, 1);
		}

	}

	private Demonstration createProtest(String name, Date start) {
		Demonstration protest = new Demonstration();
		protest.setEmail("ingatlanmarket.net@gmail.com");
		protest.setOrganizer("Istvan Benedek");
		protest.setStart(start);
		protest.setAddress("Békéscsaba");
		protest.setName(name);
		protest.setCreated(start);

		return protestRepository.save(protest);
	}

	@Test
	public void mongoDbTest() {

		List<Demonstration> demonstrations = protestRepository.findAll();

		for (Demonstration demonstration : demonstrations) {

			createVisitors(demonstration);
		}

	}

	private List<Visitor> createVisitors(Demonstration protest) {

		List<Visitor> visitors = new ArrayList<Visitor>();
		Location location = new Location();
		for (int i = 0; i < 10; i++) {

			location = new Location();
			location.setCity("City_" + i % 2);
			location.setRegion("Region_" + i % 2);
			location.setCountry("Country_" + i % 2);
			location.setLatitude(Math.random() * 10 + 1);
			location.setLongitude(Math.random() * 10 + 1);

			location = locationDao.save(location);

			Visitor visitor = registrationService
					.createVisitor(protest.getId());

			Action action = new Action(visitor);
			action.setDate(Calendar.getInstance().getTimeInMillis());
			action.setProtest(protest);
			action.setValue(1);

			actionRepository.save(action);

			action = new Action(visitor);
			action.setDate(Calendar.getInstance().getTimeInMillis());
			action.setProtest(protest);
			action.setValue(1);

			actionRepository.save(action);

			action = new Action(visitor);
			action.setDate(Calendar.getInstance().getTimeInMillis());
			action.setProtest(protest);
			action.setValue(-1);

			actionRepository.save(action);

			visitors.add(visitor);
		}

		return visitors;
	}

	@AfterClass
	public void afterClass() {

		int index;
		for (Demonstration protest : protests) {
			index = protests.indexOf(protest);

			if (index % 2 == 0) {
				protest.setEnd(Calendar.getInstance().getTime());
			}

			if (index % 2 == 1) {
				protest.setEnd(Calendar.getInstance().getTime());
				protest.setClosed(Calendar.getInstance().getTime());
			}

			protestRepository.update(protest);
		}
	}
}
