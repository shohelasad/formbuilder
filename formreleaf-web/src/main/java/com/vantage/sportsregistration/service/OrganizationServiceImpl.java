package com.vantage.sportsregistration.service;

import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.domain.*;
import com.formreleaf.repository.*;
import com.vantage.sportsregistration.dto.OrganizationForm;
import com.vantage.sportsregistration.dto.SignupForm;
import com.vantage.sportsregistration.exceptions.ResourceNotFoundException;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.WildcardQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    @PersistenceContext
    private EntityManager em;

    public Optional<Organization> findBySlug(String slug) {

        return organizationRepository.findBySlug(slug);
    }

    @Override
    public Optional<Organization> findByName(String name) {

        return organizationRepository.findByName(name);
    }

    @Override
    public boolean isNameExist(String name, Organization organization) {
        //check name is equals to current organization's name considering edit case it return false
        if (organization.getName().equals(name.trim())) {

            return false;
        } else if (findByName(name).isPresent()) {

            return true;
        }

        return false;
    }

    @Override
    public Organization saveOrganization(OrganizationForm organizationForm) {

        return organizationRepository.findOneById(organizationForm.getId())
                .map(organization -> {
                    organization.setName(organizationForm.getName());
                    organization.setPhone(organizationForm.getPhone());
                    organization.setFax(organizationForm.getFax());
                    organization.setWebsite(organizationForm.getWebsite());
                    organization.setDescription(organizationForm.getDescription());
                    organization.setSlogan(organizationForm.getSlogan());
                    organization.setSlug(StringUtils.slugify(organizationForm.getName()));
                    Address address = Optional.ofNullable(organization.getAddress()).map(a -> a).orElseGet(Address::new);
                    address.setName(organization.getName()); //TODO use case of this name?
                    address.setAddressLine1(organizationForm.getAddressLine1());
                    address.setAddressLine2(organizationForm.getAddressLine2());
                    address.setStreet(organizationForm.getStreet());
                    address.setCity(organizationForm.getCity());
                    address.setState(organizationForm.getState());
                    address.setZip(organizationForm.getZip());
                    address.setStreet(organizationForm.getStreet());
                    Optional<User> currentLoggedInUser = userService.findCurrentLoggedInUser();
                    currentLoggedInUser.ifPresent(address::setUser);
                    organization.setAddress(address);

                    return organizationRepository.save(organization);
                }).orElseThrow(() -> new ResourceNotFoundException("Organization not found by id " + organizationForm.getId()));
    }

    @Override
    public void remove(long id) {
        organizationRepository.delete(id);
    }

    @Override
    public Page<Organization> findAll(Pageable pageable) {

        return organizationRepository.findAllByDeletedFalseOrderByNameAsc(pageable);
    }

	@Override
	public Page<Organization> findAll(Pageable pageable, String searchString) {

		searchString = StringUtils.replaceNonAplhaNumericCharWithSpace(searchString);

		if (StringUtils.isEmpty(searchString)) {
			return organizationRepository.findAllByDeletedFalseOrderByNameAsc(pageable);
		}

		// get the full text entity manager
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

		BooleanQuery bq = new BooleanQuery();
		try {
			StringTokenizer st = new StringTokenizer(searchString, " ");
			while (st.hasMoreElements()) {
				String term = st.nextElement().toString() + "*";
				bq.add(new BooleanClause(new WildcardQuery(new Term("name", term)), BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("description", term)), BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("programs.name", term)),
						BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("programs.tag", term)),
						BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("address.name", term)),
						BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("address.state", term)),
						BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("address.city", term)),
						BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("address.zip", term)), BooleanClause.Occur.SHOULD));
				bq.add(new BooleanClause(new WildcardQuery(new Term("address.country", term)),
						BooleanClause.Occur.SHOULD));
			}

			FullTextQuery query = fullTextEntityManager.createFullTextQuery(bq, Organization.class);
			long totalCount = query.getResultSize();
			query.setFirstResult(pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
			List<Organization> organizationList = query.getResultList();

			return new PageImpl<>(organizationList, pageable, totalCount);
		} catch (Exception e) {
			LOGGER.error("Unable to tokenize string: ", e);

			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}
	}

    @Override
    public Agreement updateOrganizationAgreements(Agreement agreement) {
        Organization organization = getCurrentLoggedInUsersOrganization();

        return Optional.ofNullable(agreement.getId())
                .map(id -> updateAgreement(agreement, id))
                .orElseGet(() -> {
                    agreement.setOrganization(organization);

                    return agreementRepository.save(agreement);
                });
    }

    private Agreement updateAgreement(Agreement agreement, Long id) {
        return agreementRepository.findOneById(id)
                .map(agreementToUpdate -> {
                    agreementToUpdate.setDetails(agreement.getDetails());
                    agreementToUpdate.setTitle(agreement.getTitle());
                    agreementToUpdate.setMustBeAgreed(agreement.isMustBeAgreed());

                    return agreementRepository.save(agreementToUpdate);
                }).orElseThrow(() -> new ResourceNotFoundException("Agreement not found by id", agreement.getId()));
    }

    @Override
    public Set<Agreement> findAllAgreements() {
        Organization organization = getCurrentLoggedInUsersOrganization();

        return agreementRepository.findAllAgreementsByOrganization_IdAndDeletedFalse(organization.getId());
    }

    @Override
    public void deleteAgreement(Long id) {
        Agreement agreement = agreementRepository.findOne(id);
        agreementRepository.delete(agreement);
    }

    @Override
    public Policy saveOrganizationPolicy(Policy policy) {
        Organization organization = getCurrentLoggedInUsersOrganization();

        return Optional.ofNullable(policy.getId())
                .map(id -> updatePolicy(policy, id)).orElseGet(() -> {
                    policy.setOrganization(organization);

                    return policyRepository.save(policy);
                });
    }

    private Policy updatePolicy(Policy policy, Long id) {
        return policyRepository.findOneById(id)
                .map(policyToUpdate -> {
                    policyToUpdate.setDetails(policy.getDetails());
                    policyToUpdate.setTitle(policy.getTitle());

                    return policyRepository.save(policyToUpdate);
                }).orElseThrow(() -> new ResourceNotFoundException("Policy not found by id", id));
    }

    @Override
    public Set<Policy> findAllPolicies() {
        Organization organization = getCurrentLoggedInUsersOrganization();

        return policyRepository.findAllPolicyByOrganization_IdAndDeletedFalse(organization.getId());
    }

    @Override
    public void deletePolicy(Long id) {
        policyRepository.findOneById(id).ifPresent(policyRepository::delete);
    }

    @Override
    public Set<Program> findPublishedProgramByOrganization(Organization organization) {

        return programRepository.findPublishedProgramByOrganizationId(organization.getId());
    }

    @Override
    public void addNewUserToOrganization(SignupForm signupForm) {
        User user = new User();
        user.setEmail(signupForm.getEmail());
        user.setFirstName(signupForm.getFirstName());
        user.setLastName(signupForm.getLastName());
        user.setEnabled(true);
        user.setLocked(false);
        user.setSalt(StringUtils.generateRandomString(16));
        user.setHashedPassword(messageDigestPasswordEncoder.encodePassword(signupForm.getPassword(), user.getSalt()));
        user.setUserRoles(Collections.singletonList(new UserRole(user, Role.ROLE_ADMIN)).stream().collect(Collectors.toSet()));
        Organization organization = getCurrentLoggedInUsersOrganization();
        user.setOrganization(organization);
        userRepository.save(user);
    }

    @Override
    public void addNewUserToOrganizationBySlug(SignupForm signupForm) {
        User user = new User();
        user.setEmail(signupForm.getEmail());
        user.setFirstName(signupForm.getFirstName());
        user.setLastName(signupForm.getLastName());
        user.setEnabled(true);
        user.setLocked(false);
        user.setSalt(StringUtils.generateRandomString(16));
        user.setHashedPassword(messageDigestPasswordEncoder.encodePassword(signupForm.getPassword(), user.getSalt()));
        user.setUserRoles(Collections.singletonList(new UserRole(user, Role.ROLE_ADMIN)).stream().collect(Collectors.toSet()));
        Organization organization = findBySlug(signupForm.getSlug())
                .map(org -> org)
                .orElseThrow(ResourceNotFoundException::new);
        user.setOrganization(organization);

        userRepository.save(user);
    }

    @Override
    public void indexOrganization() {
        LOGGER.info("Going to index organization");

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            LOGGER.error("Unable to start indexing", e);
        }
    }

    @Override
    public Set<User> findAllUsers() {
        Set<User> users = getCurrentLoggedInUsersOrganization().getUsers();
        users.remove(userService.findCurrentLoggedInUser()
                .map(user -> user)
                .orElseThrow(UserNotFoundException::new));

        return users;
    }

    @Override
    public Set<User> findAllUsers(String slug) {

        return organizationRepository.findBySlug(slug)
                .map(Organization::getUsers)
                .orElseThrow(() -> new ResourceNotFoundException("Organization is not found by slug " + slug));
    }

    protected Organization getCurrentLoggedInUsersOrganization() {

        return userService.findCurrentLoggedInUser()
                .map(User::getOrganization)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void delete(Long id) {
        organizationRepository.findOneById(id).ifPresent(organizationRepository::delete);
    }
}
