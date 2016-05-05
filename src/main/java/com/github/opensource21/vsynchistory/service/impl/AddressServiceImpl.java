/**
 *
 */
package com.github.opensource21.vsynchistory.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.github.opensource21.vsynchistory.model.DiffResult;
import com.github.opensource21.vsynchistory.service.api.AddressService;

import ezvcard.Ezvcard;
import ezvcard.Ezvcard.ParserChainTextReader;
import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Categories;
import ezvcard.property.DateOrTimeProperty;
import ezvcard.property.Email;
import ezvcard.property.FormattedName;
import ezvcard.property.Organization;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.TextListProperty;
import ezvcard.property.Title;

/**
 * @author niels
 *
 */
@Service
public class AddressServiceImpl implements AddressService {

    private static final String DATE_FORMAT_WITH_TIME = "dd.MM.yyyy HH:mm:ss";

    @Override
    public DiffResult compare(InputStream oldAddressbook,
            InputStream newAddressbook) throws IOException {
        final Map<String, VCard> oldEntries = parseAddressbook(oldAddressbook);
        final Map<String, VCard> newEntries = parseAddressbook(newAddressbook);
        final Set<String> deletedIds =
                getValuesOnlyInFirst(oldEntries, newEntries);
        final Set<String> newIds = getValuesOnlyInFirst(newEntries, oldEntries);

        final Set<String> possibleChangedIds = new HashSet<>();
        possibleChangedIds.addAll(oldEntries.keySet());
        possibleChangedIds.removeAll(deletedIds);

        final StringBuilder message = new StringBuilder();
        final DateFormat keyDateFormat =
                new SimpleDateFormat(DATE_FORMAT_WITH_TIME);
        for (final String vCardId : deletedIds) {
            message.append("DELETED: ")
                    .append(createDescription(oldEntries.get(vCardId),
                            keyDateFormat)).append("\n");
        }
        int nrOfChangedEvents = 0;
        for (final String vCardId : possibleChangedIds) {
            final VCard oldAddress = oldEntries.get(vCardId);
            final VCard newAddress = newEntries.get(vCardId);
            final String change = getChanges(oldAddress, newAddress);
            if (StringUtils.isNotEmpty(change)) {
                nrOfChangedEvents++;
                message.append("CHANGED: ")
                        .append(createDescription(oldAddress, keyDateFormat))
                        .append("\n");
                message.append(change).append("\n");
            }

        }
        for (final String vCardId : newIds) {
            message.append("NEW: ")
                    .append(createDescription(newEntries.get(vCardId),
                            keyDateFormat)).append("\n");
        }
        return new DiffResult(deletedIds.size(), newIds.size(),
                nrOfChangedEvents, message.toString());

    }

    private Map<String, VCard> parseAddressbook(InputStream oldAddressbook)
            throws IOException {
        final ParserChainTextReader cardReader = Ezvcard.parse(oldAddressbook);
        final Map<String, VCard> result = new HashMap<>();
        for (final VCard addressCard : cardReader.all()) {
            final String key;
            if (addressCard.getUid() == null) {
                key = createDescription(addressCard, new SimpleDateFormat(DATE_FORMAT_WITH_TIME));
            } else {
                key = addressCard.getUid().getValue();
            }
            result.put(key, addressCard);
        }
        return result;
    }

    private String getChanges(VCard oldAddress, VCard newAddress) {
        final StringBuilder sb = new StringBuilder();
        final Map<String, Object> oldValues = new HashMap<>();
        final Map<String, Object> newValues = new HashMap<>();
        addToMap(oldValues, oldAddress);
        addToMap(newValues, newAddress);
        addChanges(sb, oldValues, newValues);
        return sb.toString();
    }

    private void addChanges(StringBuilder sb, Map<String, Object> oldValues,
            Map<String, Object> newValues) {
        for (final Map.Entry<String, Object> oldValue : oldValues.entrySet()) {
            if (!Objects.equals(oldValue.getValue(),
                    newValues.get(oldValue.getKey()))) {
                sb.append(oldValue.getKey()).append(':');
                sb.append(oldValue.getValue()).append("->");
                sb.append(newValues.get(oldValue.getKey()));
                sb.append(" & ");
            }

        }
    }

    private void addToMap(Map<String, Object> map, VCard address) {
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        map.put("NrOfStructurenames", address.getStructuredNames().size());
        addToMap(map, address.getStructuredName());
        addToMap(map, address.getAddresses());
        addToMap(map, "birthday", address.getBirthday(), dateFormat);
        addToMap("Categorie", map, address.getCategories());
        map.put("NrOfCategoriesList", address.getCategoriesList().size());
        addEmailsToMap(map, address.getEmails());
        addOrganistationsToMap(map, address.getOrganizations());
        addTelephoneNumbersToMap(map, address.getTelephoneNumbers());
        addTitlesToMap(map, address.getTitles());
    }

    private void addOrganistationsToMap(Map<String, Object> map,
            List<Organization> organizations) {
        Collections.sort(organizations);
        map.put("NrOfOrganizations", organizations.size());
        for (int i = 0; i < organizations.size(); i++) {
            final Organization organisation = organizations.get(i);
            addToMap("organistaion[" + i + "]", map, organisation);
        }

    }

    private void addTelephoneNumbersToMap(Map<String, Object> map,
            List<Telephone> telephoneNumbers) {
        Collections.sort(telephoneNumbers);
        map.put("NrOfTelephoneNumbers", telephoneNumbers.size());
        for (int i = 0; i < telephoneNumbers.size(); i++) {
            final Telephone telephoneNumber = telephoneNumbers.get(i);
            final List<String> telephoneNrTypes = new ArrayList<>();
            for (final TelephoneType telephoneType : telephoneNumber.getTypes()) {
                telephoneNrTypes.add(telephoneType.getValue());
            }
            Collections.sort(telephoneNrTypes);
            map.put("telephoneNumber[" + i + "].types", StringUtils.join(telephoneNrTypes, ", "));
            map.put("telephoneNumber[" + i + "].value", telephoneNumber.getText());
        }
    }

    private void addTitlesToMap(Map<String, Object> map, List<Title> titles) {
        Collections.sort(titles);
        map.put("NrOfTitles", titles.size());
        for (int i = 0; i < titles.size(); i++) {
            final Title title = titles.get(i);
            map.put("title[" + i + "].type", title.getType());
            map.put("title[" + i + "].value", title.getValue());
        }

    }

    private void addEmailsToMap(Map<String, Object> map, List<Email> emails) {
        Collections.sort(emails);
        map.put("NrOfEmails", emails.size());
        for (int i = 0; i < emails.size(); i++) {
            final Email email = emails.get(i);
            final List<String> emailTypes = new ArrayList<>();
            for (final EmailType emailType : email.getTypes()) {
                emailTypes.add(emailType.getValue());
            }
            Collections.sort(emailTypes);
            map.put("email[" + i + "].types", StringUtils.join(emailTypes, ", "));
            map.put("email[" + i + "].value", email.getValue());
        }

    }

    private void addToMap(String name, Map<String, Object> map,
            TextListProperty categories) {
        if (categories == null) {
            return;
        }
        final List<String> textProperties = categories.getValues();
        Collections.sort(textProperties);
        for (int i = 0; i < textProperties.size(); i++) {
            map.put(name + "[" + i + "]", textProperties.get(i));
        }
    }

    private void addToMap(Map<String, Object> map, String name,
            DateOrTimeProperty birthday, DateFormat format) {
        final String value =
                birthday == null ? null : format.format(birthday.getDate());
        map.put(name, value);

    }

    private void addToMap(Map<String, Object> map, List<Address> addresses) {
        map.put("NrOfAddresses", addresses.size());
        Collections.sort(addresses);
        for (int i = 0; i < addresses.size(); i++) {
            map.put("poBox[" + i + "]", addresses.get(i).getPoBox());
            map.put("extendedAddress[" + i + "]", addresses.get(i)
                    .getExtendedAddress());
            map.put("streetAddress[" + i + "]", addresses.get(i)
                    .getStreetAddress());
            map.put("locality[" + i + "]", addresses.get(i).getLocality());
            map.put("region[" + i + "]", addresses.get(i).getRegion());
            map.put("postalCode[" + i + "]", addresses.get(i).getPostalCode());
            map.put("country[" + i + "]", addresses.get(i).getCountry());
        }
    }

    private void addToMap(Map<String, Object> map, StructuredName sn) {
        map.put("family", sn.getFamily());
        map.put("given", sn.getGiven());
        map.put("prefixes", StringUtils.join(sn.getPrefixes(), ", "));
        map.put("additional", StringUtils.join(sn.getAdditional(), ", "));
        map.put("suffixes", StringUtils.join(sn.getSuffixes(), ", "));
    }

    private String createDescription(VCard vCard, DateFormat keyDateFormat) {
        final StringBuilder description = new StringBuilder();
        final FormattedName formattedName = vCard.getFormattedName();
        if (formattedName == null) {
            description.append(vCard.getStructuredName().getFamily()).append(", ");
            description.append(vCard.getStructuredName().getGiven());
        } else {
            description.append(formattedName.getValue());
        }
        description.append('@');
        for (final Categories categories : vCard.getCategoriesList()) {
            description.append('[')
                    .append(StringUtils.join(categories.getValues(), "'"))
                    .append(']');
        }
        return description.toString();
    }

    private Set<String> getValuesOnlyInFirst(
            final Map<String, VCard> firstAddressbook,
            final Map<String, VCard> secondAddressbook) {
        final Set<String> onlyFirstEntriesUid = new HashSet<>();
        onlyFirstEntriesUid.addAll(firstAddressbook.keySet());
        onlyFirstEntriesUid.removeAll(secondAddressbook.keySet());
        return onlyFirstEntriesUid;
    }

}
