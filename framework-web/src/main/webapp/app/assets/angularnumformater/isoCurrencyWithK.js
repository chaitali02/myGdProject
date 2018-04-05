/*Initial Referance taken from https://gist.github.com/thewarpaint/889690aeb21a8dfd7aba and modified as per the requirement.*/
angular.module('Utils',[]).filter('isoCurrencyWithK', ["$filter", "iso4217", function ($filter, iso4217) {
	return function (amount, fraction, currencyCode) {
    var exp = 0, rounded, currency, nagative, suffixes = ['','K', 'M', 'G', 'T', 'P', 'E'];
    //set decimal amount. Default 0
    var fractionSize = fraction === void 0 ? 0 : fraction;
    
    //check if the amount is more than 1000
    //if yes, set suffix for the amount
    if(amount >= 1000 || amount < -1000 ) {
    	
      //check if amount is nagative
      if(amount < -1000){
      	//set nagative amount as true
      	nagative = true;
      }
      
      // remove nagative amount to positive for the calculation
      amount = Math.abs(amount)
			
      //find which suffix will set for the amount based on length.
      exp = Math.floor(Math.log(amount) / Math.log(1000));
      
      //get amount after dividing 1000 according the suffix divide rule.
     	amount = amount / Math.pow(1000, exp);
      
      //apply nagative if original amount is nagative
      amount = nagative?-amount:amount;
    }
    
    //check if currency code apply or not
    //if yes then procced for the currency
    if (currencyCode) {
    		//get currency object from the list below based on the given code
    		var currency = iso4217.getCurrencyByCode(currencyCode);
        
        //apply currency symbol to the amount
					amount = $filter('currency')(amount, currency.symbol || currencyCode + ' ', fractionSize);
          
	    }else{
      	//remain same amount if the currency code not apply
        //set decimal value also
      	amount = amount.toFixed(fractionSize);
      }
      
      //return final output of the amount with "K" format include currency 
      return amount + suffixes[exp];
	};
}]).factory('iso4217', function () {
	// currency object
	var currencies = {
		'AFN': {
			text: 'Afghani',
			fraction: 2,
			symbol: '؋'
		},
		'EUR': {
			text: 'Euro',
			fraction: 2,
			symbol: '€'
		},
		'ALL': {
			text: 'Lek',
			fraction: 2,
			symbol: 'Lek'
		},
		'DZD': {
			text: 'Algerian Dinar',
			fraction: 2,
			symbol: 'د.ج'
		},
		'USD': {
			text: 'US Dollar',
			fraction: 2,
			symbol: '$'
		},
		'AOA': {
			text: 'Kwanza',
			fraction: 2,
			symbol: 'Kz'
		},
		'XCD': {
			text: 'East Caribbean Dollar',
			fraction: 2,
			symbol: '$'
		},
		'ARS': {
			text: 'Argentine Peso',
			fraction: 2,
			symbol: '$'
		},
		'AMD': {
			text: 'Armenian Dram',
			fraction: 2,
			symbol: false
		},
		'AWG': {
			text: 'Aruban Florin',
			fraction: 2,
			symbol: 'ƒ'
		},
		'AUD': {
			text: 'Australian Dollar',
			fraction: 2,
			symbol: '$'
		},
		'AZN': {
			text: 'Azerbaijani Manat',
			fraction: 2,
			symbol: 'ман'
		},
		'BSD': {
			text: 'Bahamian Dollar',
			fraction: 2,
			symbol: '$'
		},
		'BHD': {
			text: 'Bahraini Dinar',
			fraction: 3,
			symbol: 'BD'
		},
		'BDT': {
			text: 'Taka',
			fraction: 2,
			symbol: '৳'
		},
		'BBD': {
			text: 'Barbados Dollar',
			fraction: 2,
			symbol: '$'
		},
		'BYR': {
			text: 'Belarusian Ruble',
			fraction: 0,
			symbol: 'p.'
		},
		'BZD': {
			text: 'Belize Dollar',
			fraction: 2,
			symbol: 'BZ$'
		},
		'XOF': {
			text: 'CFA Franc BCEAO',
			fraction: 0,
			symbol: 'CFA'
		},
		'BMD': {
			text: 'Bermudian Dollar',
			fraction: 2,
			symbol: '$'
		},
		'BTN': {
			text: 'Ngultrum',
			fraction: 2,
			symbol: 'Nu'
		},
		'INR': {
			text: 'Indian Rupee',
			fraction: 2,
			symbol: '₹'
		},
		'BOB': {
			text: 'Boliviano',
			fraction: 2,
			symbol: '$b'
		},
		'BOV': {
			text: 'Mvdol',
			fraction: 2,
			symbol: '$b'
		},
		'BAM': {
			text: 'Convertible Mark',
			fraction: 2,
			symbol: 'KM'
		},
		'BWP': {
			text: 'Pula',
			fraction: 2,
			symbol: 'P'
		},
		'NOK': {
			text: 'Norwegian Krone',
			fraction: 2,
			symbol: 'kr'
		},
		'BRL': {
			text: 'Brazilian Real',
			fraction: 2,
			symbol: 'R$'
		},
		'BND': {
			text: 'Brunei Dollar',
			fraction: 2,
			symbol: '$'
		},
		'BGN': {
			text: 'Bulgarian Lev',
			fraction: 2,
			symbol: 'лв'
		},
		'BIF': {
			text: 'Burundian Franc',
			fraction: 0,
			symbol: 'BIF'
		},
		'KHR': {
			text: 'Riel',
			fraction: 2,
			symbol: '៛'
		},
		'XAF': {
			text: 'CFA Franc BEAC',
			fraction: 0,
			symbol: 'FCFA'
		},
		'CAD': {
			text: 'Canadian Dollar',
			fraction: 2,
			symbol: '$'
		},
		'CVE': {
			text: 'Cabo Verde Escudo',
			fraction: 2,
			symbol: '$'
		},
		'KYD': {
			text: 'Cayman Islands Dollar',
			fraction: 2,
			symbol: '$'
		},
		'CLF': {
			text: 'Unidad de Fomento',
			fraction: 4,
			symbol: false
		},
		'CLP': {
			text: 'Chilean Peso',
			fraction: 0,
			symbol: '$'
		},
		'CNY': {
			text: 'Yuan Renminbi',
			fraction: 2,
			symbol: '¥'
		},
		'COP': {
			text: 'Colombian Peso',
			fraction: 2,
			symbol: '$'
		},
		'COU': {
			text: 'Unidad de Valor Real',
			fraction: 2,
			symbol: false
		},
		'KMF': {
			text: 'Comoro Franc',
			fraction: 0,
			symbol: false
		},
		'CDF': {
			text: 'Congolese Franc',
			fraction: 2,
			symbol: false
		},
		'NZD': {
			text: 'New Zealand Dollar',
			fraction: 2,
			symbol: '$'
		},
		'CRC': {
			text: 'Costa Rican Colon',
			fraction: 2,
			symbol: '₡'
		},
		'HRK': {
			text: 'Croatian Kuna',
			fraction: 2,
			symbol: 'kn'
		},
		'CUC': {
			text: 'Peso Convertible',
			fraction: 2,
			symbol: false
		},
		'CUP': {
			text: 'Cuban Peso',
			fraction: 2,
			symbol: '₱'
		},
		'ANG': {
			text: 'Netherlands Antillean Guilder',
			fraction: 2,
			symbol: 'ƒ'
		},
		'CZK': {
			text: 'Czech Koruna',
			fraction: 2,
			symbol: 'Kč'
		},
		'DKK': {
			text: 'Danish Krone',
			fraction: 2,
			symbol: 'kr'
		},
		'DJF': {
			text: 'Djibouti Franc',
			fraction: 0,
			symbol: false
		},
		'DOP': {
			text: 'Dominican Peso',
			fraction: 2,
			symbol: 'RD$'
		},
		'EGP': {
			text: 'Egyptian Pound',
			fraction: 2,
			symbol: '£'
		},
		'SVC': {
			text: 'El Salvador Colon',
			fraction: 2,
			symbol: '$'
		},
		'ERN': {
			text: 'Nakfa',
			fraction: 2,
			symbol: false
		},
		'ETB': {
			text: 'Ethiopian Birr',
			fraction: 2,
			symbol: false
		},
		'FKP': {
			text: 'Falkland Islands Pound',
			fraction: 2,
			symbol: '£'
		},
		'FJD': {
			text: 'Fiji Dollar',
			fraction: 2,
			symbol: '$'
		},
		'XPF': {
			text: 'CFP Franc',
			fraction: 0,
			symbol: 'F'
		},
		'GMD': {
			text: 'Dalasi',
			fraction: 2,
			symbol: false
		},
		'GEL': {
			text: 'Lari',
			fraction: 2,
			symbol: false
		},
		'GHS': {
			text: 'Ghan Cedi',
			fraction: 2,
			symbol: 'GH₵'
		},
		'GIP': {
			text: 'Gibraltar Pound',
			fraction: 2,
			symbol: '£'
		},
		'GTQ': {
			text: 'Quetzal',
			fraction: 2,
			symbol: 'Q'
		},
		'GBP': {
			text: 'Pound Sterling',
			fraction: 2,
			symbol: '£'
		},
		'GNF': {
			text: 'Guinean Franc',
			fraction: 0,
			symbol: false
		},
		'GYD': {
			text: 'Guyanese Dollar',
			fraction: 2,
			symbol: '$'
		},
		'HTG': {
			text: 'Gourde',
			fraction: 2,
			symbol: false
		},
		'HNL': {
			text: 'Lempira',
			fraction: 2,
			symbol: 'L'
		},
		'HKD': {
			text: 'Hong Kong Dollar',
			fraction: 2,
			symbol: '$'
		},
		'HUF': {
			text: 'Forint',
			fraction: 2,
			symbol: 'Ft'
		},
		'ISK': {
			text: 'Iceland Krona',
			fraction: 0,
			symbol: 'kr'
		},
		'IDR': {
			text: 'Rupiah',
			fraction: 2,
			symbol: 'Rp'
		},
		'XDR': {
			text: 'SDR (Special Drawing Right)',
			fraction: 0,
			symbol: false
		},
		'IRR': {
			text: 'Iranian Rial',
			fraction: 2,
			symbol: '﷼'
		},
		'IQD': {
			text: 'Iraqi Dinar',
			fraction: 3,
			symbol: false
		},
		'ILS': {
			text: 'New Israeli Sheqel',
			fraction: 2,
			symbol: '₪'
		},
		'JMD': {
			text: 'Jamaican Dollar',
			fraction: 2,
			symbol: 'J$'
		},
		'JPY': {
			text: 'Yen',
			fraction: 0,
			symbol: '¥'
		},
		'JOD': {
			text: 'Jordanian Dinar',
			fraction: 3,
			symbol: false
		},
		'KZT': {
			text: 'Tenge',
			fraction: 2,
			symbol: '₸'
		},
		'KES': {
			text: 'Kenyan Shilling',
			fraction: 2,
			symbol: false
		},
		'KPW': {
			text: 'North Korean Won',
			fraction: 2,
			symbol: '₩'
		},
		'KRW': {
			text: 'Won',
			fraction: 0,
			symbol: '₩'
		},
		'KWD': {
			text: 'Kuwaiti Dinar',
			fraction: 3,
			symbol: 'د.ك'
		},
		'KGS': {
			text: 'Som',
			fraction: 2,
			symbol: 'som'
		},
		'LAK': {
			text: 'Kip',
			fraction: 2,
			symbol: '₭'
		},
		'LBP': {
			text: 'Lebanese Pound',
			fraction: 2,
			symbol: '£'
		},
		'LSL': {
			text: 'Loti',
			fraction: 2,
			symbol: false
		},
		'ZAR': {
			text: 'Rand',
			fraction: 2,
			symbol: 'R'
		},
		'LRD': {
			text: 'Liberian Dollar',
			fraction: 2,
			symbol: '$'
		},
		'LYD': {
			text: 'Libyan Dinar',
			fraction: 3,
			symbol: false
		},
		'CHF': {
			text: 'Swiss Franc',
			fraction: 2,
			symbol: 'CHF'
		},
		'LTL': {
			text: 'Lithuanian Litas',
			fraction: 2,
			symbol: 'Lt'
		},
		'MOP': {
			text: 'Pataca',
			fraction: 2,
			symbol: false
		},
		'MKD': {
			text: 'Denar',
			fraction: 2,
			symbol: 'ден'
		},
		'MGA': {
			text: 'Malagasy riary',
			fraction: 2,
			symbol: false
		},
		'MWK': {
			text: 'Kwacha',
			fraction: 2,
			symbol: false
		},
		'MYR': {
			text: 'Malaysian Ringgit',
			fraction: 2,
			symbol: 'RM'
		},
		'MVR': {
			text: 'Rufiyaa',
			fraction: 2,
			symbol: false
		},
		'MRO': {
			text: 'Ouguiya',
			fraction: 2,
			symbol: false
		},
		'MUR': {
			text: 'Mauritius Rupee',
			fraction: 2,
			symbol: '₨'
		},
		'XUA': {
			text: 'ADB Unit of account',
			fraction: 0,
			symbol: false
		},
		'MXN': {
			text: 'Mexican Peso',
			fraction: 2,
			symbol: '$'
		},
		'MXV': {
			text: 'Mexican Unidad de Inversion (UDI)',
			fraction: 2,
			symbol: false
		},
		'MDL': {
			text: 'Moldovan Leu',
			fraction: 2,
			symbol: false
		},
		'MNT': {
			text: 'Tugrik',
			fraction: 2,
			symbol: '₮'
		},
		'MAD': {
			text: 'Moroccan Dirham',
			fraction: 2,
			symbol: '.د.م'
		},
		'MZN': {
			text: 'Mozambique Metical',
			fraction: 2,
			symbol: 'MT'
		},
		'MMK': {
			text: 'Kyat',
			fraction: 2,
			symbol: 'K'
		},
		'NAD': {
			text: 'Namibian Dollar',
			fraction: 2,
			symbol: '$'
		},
		'NPR': {
			text: 'Nepalese Rupee',
			fraction: 2,
			symbol: '₨'
		},
		'NIO': {
			text: 'Cordob Oro',
			fraction: 2,
			symbol: 'C$'
		},
		'NGN': {
			text: 'Naira',
			fraction: 2,
			symbol: '₦'
		},
		'OMR': {
			text: 'Rial Omani',
			fraction: 3,
			symbol: '﷼'
		},
		'PKR': {
			text: 'Pakistan Rupee',
			fraction: 2,
			symbol: '₨'
		},
		'PAB': {
			text: 'Balboa',
			fraction: 2,
			symbol: 'B/.'
		},
		'PGK': {
			text: 'Kina',
			fraction: 2,
			symbol: 'K'
		},
		'PYG': {
			text: 'Guarani',
			fraction: 0,
			symbol: 'Gs'
		},
		'PEN': {
			text: 'Nuevo Sol',
			fraction: 2,
			symbol: 'S/.'
		},
		'PHP': {
			text: 'Philippine Peso',
			fraction: 2,
			symbol: '₱'
		},
		'PLN': {
			text: 'Zloty',
			fraction: 2,
			symbol: 'zł'
		},
		'QAR': {
			text: 'Qatari Rial',
			fraction: 2,
			symbol: '﷼'
		},
		'RON': {
			text: 'New Romanian Leu',
			fraction: 2,
			symbol: 'lei'
		},
		'RUB': {
			text: 'Russian Ruble',
			fraction: 2,
			symbol: 'руб'
		},
		'RWF': {
			text: 'Rwandan Franc',
			fraction: 0,
			symbol: 'R₣'
		},
		'SHP': {
			text: 'Saint Helena Pound',
			fraction: 2,
			symbol: '£'
		},
		'WST': {
			text: 'Tala',
			fraction: 2,
			symbol: '$'
		},
		'STD': {
			text: 'Dobra',
			fraction: 2,
			symbol: false
		},
		'SAR': {
			text: 'Saudi Riyal',
			fraction: 2,
			symbol: '﷼'
		},
		'RSD': {
			text: 'Serbian Dinar',
			fraction: 2,
			symbol: 'Дин.'
		},
		'SCR': {
			text: 'Seychelles Rupee',
			fraction: 2,
			symbol: '₨'
		},
		'SLL': {
			text: 'Leone',
			fraction: 2,
			symbol: 'Le'
		},
		'SGD': {
			text: 'Singapore Dollar',
			fraction: 2,
			symbol: '$'
		},
		'XSU': {
			text: 'Sucre',
			fraction: 0,
			symbol: false
		},
		'SBD': {
			text: 'Solomon Islands Dollar',
			fraction: 2,
			symbol: '$'
		},
		'SOS': {
			text: 'Somali Shilling',
			fraction: 2,
			symbol: 'S'
		},
		'SSP': {
			text: 'South Sudanese Pound',
			fraction: 2,
			symbol: false
		},
		'LKR': {
			text: 'Sri Lankan Rupee',
			fraction: 2,
			symbol: '₨'
		},
		'SDG': {
			text: 'Sudanese Pound',
			fraction: 2,
			symbol: false
		},
		'SRD': {
			text: 'Surinam Dollar',
			fraction: 2,
			symbol: '$'
		},
		'SZL': {
			text: 'Lilangeni',
			fraction: 2,
			symbol: false
		},
		'SEK': {
			text: 'Swedish Krona',
			fraction: 2,
			symbol: 'kr'
		},
		'CHE': {
			text: 'WIR Euro',
			fraction: 2,
			symbol: false
		},
		'CHW': {
			text: 'WIR Franc',
			fraction: 2,
			symbol: false
		},
		'SYP': {
			text: 'Syrian Pound',
			fraction: 2,
			symbol: '£'
		},
		'TWD': {
			text: 'New Taiwan Dollar',
			fraction: 2,
			symbol: 'NT$'
		},
		'TJS': {
			text: 'Somoni',
			fraction: 2,
			symbol: false
		},
		'TZS': {
			text: 'Tanzanian Shilling',
			fraction: 2,
			symbol: false
		},
		'THB': {
			text: 'Baht',
			fraction: 2,
			symbol: '฿'
		},
		'TOP': {
			text: 'Pa’anga',
			fraction: 2,
			symbol: false
		},
		'TTD': {
			text: 'Trinidad and Tobago Dollar',
			fraction: 2,
			symbol: 'TT$'
		},
		'TND': {
			text: 'Tunisian Dinar',
			fraction: 3,
			symbol: 'DT'
		},
		'TRY': {
			text: 'Turkish Lira',
			fraction: 2,
			symbol: '₺'
		},
		'TMT': {
			text: 'Turkmenistan New Manat',
			fraction: 2,
			symbol: false
		},
		'UGX': {
			text: 'Ugandan Shilling',
			fraction: 0,
			symbol: false
		},
		'UAH': {
			text: 'Hryvnia',
			fraction: 2,
			symbol: '₴'
		},
		'AED': {
			text: 'UAE Dirham',
			fraction: 2,
			symbol: 'د.إ'
			//symbol: false
		},
		'USN': {
			text: 'US Dollar (Next day)',
			fraction: 2,
			symbol: false
		},
		'UYI': {
			text: 'Uruguay Peso en Unidades Indexadas (URUIURUI)',
			fraction: 0,
			symbol: false
		},
		'UYU': {
			text: 'Peso Uruguayo',
			fraction: 2,
			symbol: '$U'
		},
		'UZS': {
			text: 'Uzbekistan Sum',
			fraction: 2,
			symbol: 'som'
		},
		'VUV': {
			text: 'Vatu',
			fraction: 0,
			symbol: false
		},
		'VEF': {
			text: 'Bolivar',
			fraction: 2,
			symbol: 'Bs'
		},
		'VND': {
			text: 'Dong',
			fraction: 0,
			symbol: '₫'
		},
		'YER': {
			text: 'Yemeni Rial',
			fraction: 2,
			symbol: '﷼'
		},
		'ZMW': {
			text: 'Zambian Kwacha',
			fraction: 2,
			symbol: false
		},
		'ZWL': {
			text: 'Zimbabwe Dollar',
			fraction: 2,
			symbol: '$'
		}
	};

	return {
		/**
   * retrieves the object holding currency, code and fraction information about a currency.
   *
   * @param string code
   * @return object
   */
		getCurrencyByCode: function getCurrencyByCode(code) {
			if (!code || typeof code !== 'string') {
				return;
			}

			return currencies[code.toUpperCase()];
		},
		/**
   * retrives all available currenies.
   *
   * @return object
   */
		getCurrencies: function getCurrencies() {
			return currencies;
		}
	};
});
