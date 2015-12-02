//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.impl.gwt;

/********************************************************************
 * Interface to represent the constants contained in resource bundle:
 * 'GewtStrings.properties'.
 */
public interface GewtStrings
	extends com.google.gwt.i18n.client.ConstantsWithLookup
{
	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Translated "Und".
	 *
	 * @return translated "Und"
	 */
	@DefaultStringValue("Und")
	@Key("itmTableFilterJoinAnd")
	String itmTableFilterJoinAnd();

	/***************************************
	 * Translated "Oder".
	 *
	 * @return translated "Oder"
	 */
	@DefaultStringValue("Oder")
	@Key("itmTableFilterJoinOr")
	String itmTableFilterJoinOr();

	/***************************************
	 * Translated "Keine Daten verfügbar".
	 *
	 * @return translated "Keine Daten verfügbar"
	 */
	@DefaultStringValue("Keine Daten verfügbar")
	@Key("lblNoEwtChartData")
	String lblNoEwtChartData();

	/***************************************
	 * Translated "Daten nicht verfügbar".
	 *
	 * @return translated "Daten nicht verfügbar"
	 */
	@DefaultStringValue("Daten nicht verfügbar")
	@Key("msgTableModelError")
	String msgTableModelError();

	/***************************************
	 * Translated "Filter anwenden".
	 *
	 * @return translated "Filter anwenden"
	 */
	@DefaultStringValue("Filter anwenden")
	@Key("ttApplyTableFilter")
	String ttApplyTableFilter();

	/***************************************
	 * Translated "Auswahl entfernen".
	 *
	 * @return translated "Auswahl entfernen"
	 */
	@DefaultStringValue("Auswahl entfernen")
	@Key("ttClearSelection")
	String ttClearSelection();

	/***************************************
	 * Translated "Filter entfernen".
	 *
	 * @return translated "Filter entfernen"
	 */
	@DefaultStringValue("Filter entfernen")
	@Key("ttClearTableFilter")
	String ttClearTableFilter();

	/***************************************
	 * Translated "Tabelleninhalt herunterladen".
	 *
	 * @return translated "Tabelleninhalt herunterladen"
	 */
	@DefaultStringValue("Tabelleninhalt herunterladen")
	@Key("ttDownloadTableContent")
	String ttDownloadTableContent();

	/***************************************
	 * Translated "Erste Seite".
	 *
	 * @return translated "Erste Seite"
	 */
	@DefaultStringValue("Erste Seite")
	@Key("ttFirstTablePage")
	String ttFirstTablePage();

	/***************************************
	 * Translated "Hour".
	 *
	 * @return translated "Hour"
	 */
	@DefaultStringValue("Hour")
	@Key("ttGewtDatePickerHour")
	String ttGewtDatePickerHour();

	/***************************************
	 * Translated "Minute".
	 *
	 * @return translated "Minute"
	 */
	@DefaultStringValue("Minute")
	@Key("ttGewtDatePickerMinute")
	String ttGewtDatePickerMinute();

	/***************************************
	 * Translated "Time".
	 *
	 * @return translated "Time"
	 */
	@DefaultStringValue("Time")
	@Key("ttGewtDatePickerTime")
	String ttGewtDatePickerTime();

	/***************************************
	 * Translated "Letzte Seite".
	 *
	 * @return translated "Letzte Seite"
	 */
	@DefaultStringValue("Letzte Seite")
	@Key("ttLastTablePage")
	String ttLastTablePage();

	/***************************************
	 * Translated "Tabellenhöhe fest/variabel".
	 *
	 * @return translated "Tabellenhöhe fest/variabel"
	 */
	@DefaultStringValue("Tabellenhöhe fest/variabel")
	@Key("ttLockTableSize")
	String ttLockTableSize();

	/***************************************
	 * Translated "Nächste Seite".
	 *
	 * @return translated "Nächste Seite"
	 */
	@DefaultStringValue("Nächste Seite")
	@Key("ttNextTablePage")
	String ttNextTablePage();

	/***************************************
	 * Translated "Vorherige Seite".
	 *
	 * @return translated "Vorherige Seite"
	 */
	@DefaultStringValue("Vorherige Seite")
	@Key("ttPrevTablePage")
	String ttPrevTablePage();

	/***************************************
	 * Translated "Filterbedingung hinzufügen".
	 *
	 * @return translated "Filterbedingung hinzufügen"
	 */
	@DefaultStringValue("Filterbedingung hinzufügen")
	@Key("ttTableFilterAdd")
	String ttTableFilterAdd();

	/***************************************
	 * Translated "Filter anwenden".
	 *
	 * @return translated "Filter anwenden"
	 */
	@DefaultStringValue("Filter anwenden")
	@Key("ttTableFilterApply")
	String ttTableFilterApply();

	/***************************************
	 * Translated "Filteränderungen verwerfen".
	 *
	 * @return translated "Filteränderungen verwerfen"
	 */
	@DefaultStringValue("Filteränderungen verwerfen")
	@Key("ttTableFilterCancel")
	String ttTableFilterCancel();

	/***************************************
	 * Translated "Vergleichsoperator für die Filterung".
	 *
	 * @return translated "Vergleichsoperator für die Filterung"
	 */
	@DefaultStringValue("Vergleichsoperator für die Filterung")
	@Key("ttTableFilterComparison")
	String ttTableFilterComparison();

	/***************************************
	 * Translated "Filterbedingung entfernen".
	 *
	 * @return translated "Filterbedingung entfernen"
	 */
	@DefaultStringValue("Filterbedingung entfernen")
	@Key("ttTableFilterRemove")
	String ttTableFilterRemove();

	/***************************************
	 * Translated "Filterkriterium".
	 *
	 * @return translated "Filterkriterium"
	 */
	@DefaultStringValue("Filterkriterium")
	@Key("ttTableFilterValue")
	String ttTableFilterValue();
}
