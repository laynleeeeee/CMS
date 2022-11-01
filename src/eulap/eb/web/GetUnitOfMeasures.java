package eulap.eb.web;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.service.UnitMeasurementService;
import eulap.eb.service.UomConversionService;

/**
 * A controller class that retrieves the unit of measurements.

 *
 */
@Controller
@RequestMapping ("getUnitMeasurements")
public class GetUnitOfMeasures {
	@Autowired
	private UnitMeasurementService unitMeasurementService;
	@Autowired
	private UomConversionService uomConversionService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getUnitOfMeasurements (@RequestParam(value="name", required=false) String name,
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam(value="noLimit", required=false) Boolean noLimit,
			@RequestParam(value="isAddNone", required=false) Boolean isAddNone){
		List<UnitMeasurement> unitMeasurements = new ArrayList<UnitMeasurement>();
		if(isAddNone != null) {
			if(isAddNone) {
				//Search for AR Lines without measurement
				UnitMeasurement measurement = new UnitMeasurement();
				measurement.setId(0);
				measurement.setName("None");
				unitMeasurements.add(measurement);
			}
		}
		unitMeasurements.addAll(unitMeasurementService.getActiveUnitMeasurements(name, id, noLimit));
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(unitMeasurements, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method=RequestMethod.GET, value="/getUom")
	public @ResponseBody String getUOM(@RequestParam(value="name") String name) {
		UnitMeasurement uom = unitMeasurementService.getUMByName(name);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(uom, jsonConfig);
		return uom == null ? "No Measurement found" : jsonObject.toString();
	}

	/**
	 * Get the value of the UOM Conversion.
	 * @param uomFromId The id of the UOM From
	 * @return The value of the UOM Conversion.
	 */
	@RequestMapping(method=RequestMethod.GET, value="/convert")
	public @ResponseBody String convert(@RequestParam(value="uomFromId") Integer uomFromId,
			@RequestParam(value="uomToId") Integer uomToId,
			@RequestParam(value="quantity") String strQuantity) {
		double convertedQty = uomConversionService.convert(uomFromId, uomToId, strQuantity);
		return NumberFormatUtil.formatNumber(convertedQty, 6);
	}
}
