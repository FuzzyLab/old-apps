package com.fuzzylabs.unitcoverter;

import java.text.DecimalFormat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, Adapter,
		OnItemSelectedListener {

	private Spinner SpinnerUnit;
	private EditText inputValue;
	private Spinner SpinnerFrom;
	private Spinner SpinnerTo;
	private Button ButtonConvert;
	private TextView ResultView;
	private TextView Display;
	ArrayAdapter<String> unitArray;
	ArrayAdapter<String> unitArrayAdapter;
	private String unitFrom;
	private String unitTo;
	private static MainActivity instance;
	private Strategy currentStrategy;
	private Strategy lastStrategy;
	private Button toggle;
	double factor;
	double value;
	DecimalFormat df = new DecimalFormat("#.##");

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);

		SpinnerUnit = (Spinner) findViewById(R.id.SpinnerUnit);
		SpinnerUnit.setOnItemSelectedListener(this);

		unitArray = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		unitArray
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitArray.add(getResources().getString(R.string.unit1));
		unitArray.add(getResources().getString(R.string.unit2));
		unitArray.add(getResources().getString(R.string.unit3));
		unitArray.add(getResources().getString(R.string.unit4));
		unitArray.add(getResources().getString(R.string.unit5));
		unitArray.add(getResources().getString(R.string.unit6));
		unitArray.add(getResources().getString(R.string.unit7));
		unitArray.add(getResources().getString(R.string.unit8));
		unitArray.add(getResources().getString(R.string.unit9));
		unitArray.add(getResources().getString(R.string.unit10));
		unitArray.add(getResources().getString(R.string.unit11));
		unitArray.add(getResources().getString(R.string.unit12));
		unitArray.add(getResources().getString(R.string.unit13));
		unitArray.add(getResources().getString(R.string.unit14));
		unitArray.add(getResources().getString(R.string.unit15));
		unitArray.setNotifyOnChange(true);
		SpinnerUnit.setAdapter(unitArray);

		SpinnerFrom = (Spinner) findViewById(R.id.SpinnerFrom);
		SpinnerFrom.setOnItemSelectedListener(this);
		SpinnerTo = (Spinner) findViewById(R.id.SpinnerTo);
		SpinnerTo.setOnItemSelectedListener(this);

		unitArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		unitArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SpinnerFrom.setAdapter(unitArrayAdapter);
		SpinnerTo.setAdapter(unitArrayAdapter);

		unitArrayAdapter.setNotifyOnChange(true);

		ResultView = (TextView) findViewById(R.id.TextViewResult);

		Display = (TextView) findViewById(R.id.display);
		Display.setText("Type Selected : Temperature");

		ButtonConvert = (Button) findViewById(R.id.Button01);
		toggle = (Button) findViewById(R.id.Button02);

		ButtonConvert.setOnClickListener(this);
		toggle.setOnClickListener(this);

		inputValue = (EditText) findViewById(R.id.EditTextValue);

		factor = 0;

		inputValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable statusText) {
				unitFrom = (String) (SpinnerFrom.getSelectedItem().toString());
				unitTo = (String) (SpinnerTo.getSelectedItem().toString());
				onClick(ButtonConvert);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});

		// initialization
		currentStrategy = new TemperatureStrategy();
		lastStrategy = currentStrategy;
		instance = this;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.unitcoverter";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.unitcoverter";

			String body = "---Unit Converter---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Unit Converter"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static MainActivity getInstance() {
		return instance;
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {

		if (v.getParent() == SpinnerUnit) {

			switch (position) {

			case 0:
				setStrategy(new TemperatureStrategy());
				Display.setText("Type Selected : Temperature");
				break;

			case 1:
				setStrategy(new WeightStrategy());
				Display.setText("Type Selected : Weight");
				break;

			case 2:
				setStrategy(new LengthStrategy());
				Display.setText("Type Selected : Length");
				break;

			case 3:
				setStrategy(new PowerStrategy());
				Display.setText("Type Selected : Power");
				break;

			case 4:
				setStrategy(new EnergyStrategy());
				Display.setText("Type Selected : Energy");
				break;

			case 5:
				setStrategy(new VelocityStrategy());
				Display.setText("Type Selected : Velocity");
				break;

			case 6:
				setStrategy(new AreaStrategy());
				Display.setText("Type Selected : Area");
				break;

			case 7:
				setStrategy(new VolumeStrategy());
				Display.setText("Type Selected : Volume");
				break;

			case 8:
				setStrategy(new FlowStrategy());
				Display.setText("Type Selected : Flow");
				break;
			case 9:
				setStrategy(new TimeStrategy());
				Display.setText("Type Selected : Time");
				break;
			case 10:
				setStrategy(new DataStrategy());
				Display.setText("Type Selected : Data Storage");
				break;
			case 11:
				setStrategy(new PressureStrategy());
				Display.setText("Type Selected : Pressure");
				break;
			case 12:
				setStrategy(new ForceStrategy());
				Display.setText("Type Selected : Force");
				break;
			case 13:
				setStrategy(new AngleStrategy());
				Display.setText("Type Selected : Angle");
				break;
			case 14:
				setStrategy(new DensityStrategy());
				Display.setText("Type Selected : Density");
				break;
			}

			fillFromToSpinner(position);

			SpinnerFrom.setSelection(0);
			SpinnerTo.setSelection(0);

			unitFrom = (String) (SpinnerFrom.getItemAtPosition(0).toString());
			unitTo = (String) (SpinnerTo.getItemAtPosition(0).toString());

			// reset the result
			ResultView.setText("");
		}

		else if (v.getParent() == SpinnerFrom) {
			unitFrom = (String) (SpinnerFrom.getSelectedItem().toString());
			unitTo = (String) (SpinnerTo.getSelectedItem().toString());
			onClick(ButtonConvert);
		}

		else if (v.getParent() == SpinnerTo) {
			unitFrom = (String) (SpinnerFrom.getSelectedItem().toString());
			unitTo = (String) (SpinnerTo.getSelectedItem().toString());
			onClick(ButtonConvert);
		}

	}

	private void setStrategy(Strategy s) {

		lastStrategy = currentStrategy;
		currentStrategy = s;
		// make the last strategy eligible for garbage collection
		lastStrategy = null;
	}

	private void fillFromToSpinner(int position) {

		switch (position) {
		case 0:
			fillSpinnerWithTempUnit();
			break;

		case 1:
			fillSpinnerWithWeightUnit();
			break;

		case 2:
			fillSpinnerWithLengthUnit();
			break;

		case 3:
			fillSpinnerWithPowerUnit();
			break;

		case 4:
			fillSpinnerWithEnergyUnit();
			break;

		case 5:
			fillSpinnerWithVelocityUnit();
			break;

		case 6:
			fillSpinnerWithAreaUnit();
			break;

		case 7:
			fillSpinnerWithVolumeUnit();
			break;

		case 8:
			fillSpinnerWithFlowUnit();
			break;
		case 9:
			fillSpinnerWithTimeUnit();
			break;
		case 10:
			fillSpinnerWithDataUnit();
			break;
		case 11:
			fillSpinnerWithPressureUnit();
			break;
		case 12:
			fillSpinnerWithForceUnit();
			break;
		case 13:
			fillSpinnerWithAngleUnit();
			break;
		case 14:
			fillSpinnerWithDensityUnit();
			break;
		}
	}

	private void fillSpinnerWithDensityUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(
				R.string.densitykgpercubicm));
		unitArrayAdapter.add(getResources().getString(
				R.string.densitygrampercubiccm));
		unitArrayAdapter.add(getResources().getString(
				R.string.densitymgpercubicmm));
		unitArrayAdapter.add(getResources().getString(R.string.densitykgperl));
		unitArrayAdapter
				.add(getResources().getString(R.string.densitygramperl));
		unitArrayAdapter.add(getResources().getString(
				R.string.densitypoundpercubicft));
		unitArrayAdapter.add(getResources().getString(
				R.string.densitypoundpercubicinch));
		unitArrayAdapter.add(getResources().getString(
				R.string.densitypoundpergallon));
		unitArrayAdapter.add(getResources().getString(
				R.string.densityouncepercubicft));
		unitArrayAdapter.add(getResources().getString(
				R.string.densityouncepercubicinch));
		unitArrayAdapter.add(getResources().getString(
				R.string.densityouncepergallon));
		unitArrayAdapter.add(getResources().getString(
				R.string.densitytonpercubicyard));
		unitArrayAdapter.add(getResources().getString(
				R.string.densitypsiperthft));
	}

	private void fillSpinnerWithAngleUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.angledegree));
		unitArrayAdapter.add(getResources().getString(R.string.angleradian));
		unitArrayAdapter.add(getResources().getString(R.string.angleminute));
		unitArrayAdapter.add(getResources().getString(R.string.anglesecond));
		unitArrayAdapter
				.add(getResources().getString(R.string.anglerevolution));
		unitArrayAdapter.add(getResources().getString(R.string.anglecircle));
		unitArrayAdapter
				.add(getResources().getString(R.string.anglerightangle));
		unitArrayAdapter.add(getResources().getString(R.string.anglesextant));
		unitArrayAdapter.add(getResources().getString(R.string.angleoctant));
	}

	private void fillSpinnerWithForceUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.forcenewton));
		unitArrayAdapter
				.add(getResources().getString(R.string.forcekilonewton));
		unitArrayAdapter.add(getResources().getString(R.string.forcedyne));
		unitArrayAdapter.add(getResources().getString(R.string.forcejouleperm));
		unitArrayAdapter.add(getResources().getString(R.string.forcekgforce));
		unitArrayAdapter.add(getResources().getString(R.string.forcegramforce));
		unitArrayAdapter
				.add(getResources().getString(R.string.forcepoundforce));
		unitArrayAdapter
				.add(getResources().getString(R.string.forceounceforce));
	}

	private void fillSpinnerWithTempUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources()
				.getString(R.string.temperatureunitc));
		unitArrayAdapter.add(getResources()
				.getString(R.string.temperatureunitf));
		unitArrayAdapter.add(getResources()
				.getString(R.string.temperatureunitk));
		unitArrayAdapter.notifyDataSetChanged();
	}

	private void fillSpinnerWithWeightUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.weightunitkg));
		unitArrayAdapter.add(getResources().getString(R.string.weightunitgm));
		unitArrayAdapter.add(getResources().getString(R.string.weightunitlb));
		unitArrayAdapter
				.add(getResources().getString(R.string.weightunitounce));
		unitArrayAdapter.add(getResources().getString(R.string.weightunitmg));
		unitArrayAdapter
				.add(getResources().getString(R.string.weightunitstone));
		unitArrayAdapter.add(getResources().getString(
				R.string.weightunitmetricton));
		unitArrayAdapter.add(getResources().getString(
				R.string.weightunitquintal));
		unitArrayAdapter.add(getResources().getString(R.string.weightunitdyne));
		unitArrayAdapter.add(getResources().getString(
				R.string.weightunitquarter));
		unitArrayAdapter.add(getResources()
				.getString(R.string.weightunitcarrat));
		unitArrayAdapter.notifyDataSetChanged();
	}

	private void fillSpinnerWithLengthUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitmile));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitkm));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitm));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitcm));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitmm));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitinch));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitfeet));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunityard));
		unitArrayAdapter.add(getResources().getString(
				R.string.lengthunitfurlong));
		unitArrayAdapter
				.add(getResources().getString(R.string.lengthunitchain));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitrod));
		unitArrayAdapter
				.add(getResources().getString(R.string.lengthunitperch));
		unitArrayAdapter.add(getResources().getString(R.string.lengthunitpole));
	}

	private void fillSpinnerWithPowerUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.powerunitwatts));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunithorseposer));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitkilowatts));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitmegawatt));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitbtuperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitbtupermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitbtupersec));
		unitArrayAdapter.add(getResources().getString(R.string.powerunitton));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitkilocalorieperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitkilocaloriepermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitkilocaloriepersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitcaloriepermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitcaloriepersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitergpersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitkilojoulespersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.powerunitjoulespersec));
	}

	private void fillSpinnerWithEnergyUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitcalories));
		unitArrayAdapter.add(getResources()
				.getString(R.string.energyunitjoules));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitkilocalories));
		unitArrayAdapter.add(getResources().getString(R.string.energyuniterg));
		unitArrayAdapter.add(getResources()
				.getString(R.string.energyunitkwhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitwatthour));
		unitArrayAdapter.add(getResources()
				.getString(R.string.energyunithphour));
		unitArrayAdapter.add(getResources().getString(R.string.energyunitbtu));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitkiloton));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitnewtonm));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitgramforcem));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitkgforcem));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitpoundforceft));
		unitArrayAdapter.add(getResources().getString(
				R.string.energyunitounceforceinch));
		unitArrayAdapter
				.add(getResources().getString(R.string.energyunittherm));

	}

	private void fillSpinnerWithVelocityUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources()
				.getString(R.string.velocityunitkmph));
		unitArrayAdapter.add(getResources().getString(
				R.string.velocityunitmilesperh));
		unitArrayAdapter.add(getResources().getString(
				R.string.velocityunitmeterpers));
		unitArrayAdapter.add(getResources().getString(
				R.string.velocityunitfeetpers));
		unitArrayAdapter.add(getResources().getString(
				R.string.velocityunityardsperh));
		unitArrayAdapter.add(getResources().getString(
				R.string.velocityunitinchpers));
		unitArrayAdapter.add(getResources().getString(
				R.string.velocityunitknots));
	}

	private void fillSpinnerWithAreaUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.areaunitsqkm));
		unitArrayAdapter
				.add(getResources().getString(R.string.areaunitsqmiles));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitsqm));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitsqcm));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitsqmm));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitsqyard));
		unitArrayAdapter
				.add(getResources().getString(R.string.areaunithectare));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitacre));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitrood));
		unitArrayAdapter
				.add(getResources().getString(R.string.areaunitsqporch));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitsqpole));
		unitArrayAdapter.add(getResources().getString(R.string.areaunitplaza));
		unitArrayAdapter
				.add(getResources().getString(R.string.areaunitsection));
	}

	private void fillSpinnerWithVolumeUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources()
				.getString(R.string.volumeunitlitres));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunitmillilitres));
		unitArrayAdapter.add(getResources()
				.getString(R.string.volumeunitcubicm));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunitcubiccm));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunitcubicmm));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunitcubicfeet));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunitcubicyard));
		unitArrayAdapter.add(getResources()
				.getString(R.string.volumeunitgallon));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunitoilbarrel));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunittonofwater));
		unitArrayAdapter.add(getResources().getString(
				R.string.volumeunitpintsus));
	}

	private void fillSpinnerWithDataUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.datagigabyte));
		unitArrayAdapter.add(getResources().getString(R.string.datagigabit));
		unitArrayAdapter.add(getResources().getString(R.string.datamegabyte));
		unitArrayAdapter.add(getResources().getString(R.string.datamegabit));
		unitArrayAdapter.add(getResources().getString(R.string.datakilobyte));
		unitArrayAdapter.add(getResources().getString(R.string.datakilobit));
		unitArrayAdapter.add(getResources().getString(R.string.databyte));
		unitArrayAdapter.add(getResources().getString(R.string.databit));
	}

	private void fillSpinnerWithTimeUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.timeyear));
		unitArrayAdapter.add(getResources().getString(R.string.timemonth));
		unitArrayAdapter.add(getResources().getString(R.string.timeweek));
		unitArrayAdapter.add(getResources().getString(R.string.timeday));
		unitArrayAdapter.add(getResources().getString(R.string.timehour));
		unitArrayAdapter.add(getResources().getString(R.string.timeminute));
		unitArrayAdapter.add(getResources().getString(R.string.timesec));
		unitArrayAdapter.add(getResources().getString(R.string.timemillisec));
		unitArrayAdapter.add(getResources().getString(R.string.timemicrosec));
		unitArrayAdapter.add(getResources().getString(R.string.timenanosec));
	}

	private void fillSpinnerWithPressureUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(R.string.pressurepascal));
		unitArrayAdapter.add(getResources().getString(
				R.string.pressureatmosphere));
		unitArrayAdapter.add(getResources().getString(R.string.pressurebar));
		unitArrayAdapter.add(getResources().getString(
				R.string.pressuremercurycm));
		unitArrayAdapter.add(getResources().getString(R.string.pressurebarad));
		unitArrayAdapter.add(getResources().getString(R.string.pressurebarye));
		unitArrayAdapter.add(getResources().getString(
				R.string.pressuredynepersquarecm));
		unitArrayAdapter.add(getResources().getString(
				R.string.pressurekgpersquarem));
		unitArrayAdapter.add(getResources().getString(
				R.string.pressuremlofwater));
		unitArrayAdapter.add(getResources().getString(
				R.string.pressureouncepersquareinch));
		unitArrayAdapter.add(getResources().getString(
				R.string.pressurepoundpersquareinch));
		unitArrayAdapter.add(getResources().getString(R.string.pressuretorr));
	}

	private void fillSpinnerWithFlowUnit() {
		unitArrayAdapter.clear();
		unitArrayAdapter.add(getResources().getString(
				R.string.flowmetercubepersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowmetercubepermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowmetercubeperhour));
		unitArrayAdapter
				.add(getResources().getString(R.string.flowliterpersec));
		unitArrayAdapter
				.add(getResources().getString(R.string.flowliterpermin));
		unitArrayAdapter.add(getResources()
				.getString(R.string.flowliterperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowcubicfeetperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowcubicfeetpermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowcubicfeetpersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowcubicyardperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowcubicyardpermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowcubicyardpersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowbarrelperhour));
		unitArrayAdapter.add(getResources()
				.getString(R.string.flowbarrelpermin));
		unitArrayAdapter.add(getResources()
				.getString(R.string.flowbarrelpersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowgallonukperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowgallonukpermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowgallonukpersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowgallonusperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowgallonuspermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowgallonuspersec));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowtonofwaterperhour));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowtonofwaterpermin));
		unitArrayAdapter.add(getResources().getString(
				R.string.flowtonofwaterpersec));
	}

	@Override
	public void onClick(View v) {

		if (v == ButtonConvert) {
			if (!inputValue.getText().toString().equals("")) {
				double in = Double.parseDouble(inputValue.getText().toString());
				double result = currentStrategy.Convert(unitFrom, unitTo, in);
				factor = result / in;
				DecimalFormat df = new DecimalFormat("#.########");
				ResultView.setText(df.format(result).toString());
			} else {
				ResultView.setText("");
			}
		} else if (v == toggle) {
			int from = SpinnerFrom.getSelectedItemPosition();
			int to = SpinnerTo.getSelectedItemPosition();
			SpinnerFrom.setSelection(to);
			SpinnerTo.setSelection(from);
			unitFrom = (String) (SpinnerFrom.getSelectedItem().toString());
			unitTo = (String) (SpinnerTo.getSelectedItem().toString());
			onClick(ButtonConvert);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
