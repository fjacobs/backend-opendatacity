package com.dynacore.livemap.guidancesign.model;


//For a full description see: https://open.data.amsterdam.nl/uploads/actuele_beschikbaarheid_parkeergarages/Beschrijving%20Dataset%20-%20Actuele%20beschikbaarheid%20Parkeergarages%20v2.pdf

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class InnerDisplayModel {

//    "ParkingguidanceDisplay":[  
//                              {  
//                                 "Id":"000001091",
//                                 "OutputDescription":"VOL",
//                                 "Description":"A0201_VVX_P+R Arena",
//                                 "Type":"VVX",
//                                 "Output":"VOL"
//                              }
//                           ]
	@JsonProperty("Id")
	private UUID id;
	@JsonProperty("Description")
	private String description;

	//Display type.
	//Can be one of following three types:
	// VVX 			 (Can display ‘Vol’ or ‘Vrij’)
	// VVXNUMERIC	 (Can display ‘Vol’, ‘Vrij’ and/or available free parking places)
	// ROTATIONPANEL (The display text can be changed through rotating panel.)
	@JsonProperty("Type")
	private String type;

	//Content of display output
	@JsonProperty("Output")
	private String output;

	//Description of the content of field 'output'
	//In most cases this is equal to 'output'
	@JsonProperty("OutputDescription")
	private String outputDescription;

	public InnerDisplayModel() {
	}

	private InnerDisplayModel(Builder builder) {
		setId(builder.id);
		setDescription(builder.description);
		setType(builder.type);
		setOutput(builder.output);
		setOutputDescription(builder.outputDescription);
	}

	public static final class Builder {
		private UUID id;
		private String description;
		private String type;
		private String output;
		private String outputDescription;

		public Builder() {
		}

		public Builder id(UUID val) {
			id = val;
			return this;
		}

		public Builder description(String val) {
			description = val;
			return this;
		}

		public Builder type(String val) {
			type = val;
			return this;
		}

		public Builder output(String val) {
			output = val;
			return this;
		}

		public Builder outputDescription(String val) {
			outputDescription = val;
			return this;
		}

		public InnerDisplayModel build() {
			return new InnerDisplayModel(this);
		}
	}
}
