import i18n from '.././lang'
var validateMobile = (rule, value, callback) => {
    if (!value) {
        callback(new Error(i18n.t('validate.phone')));
    } else if (!/^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\d{8}$/.test(value)) {
        callback(new Error(i18n.t('validate.phone_format')));
    } else {
        callback();
    }
};
var validateEmail = (rule, value, callback) => {
    if (/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/.test(value)) {
        callback();
    } else {
        callback(new Error(i18n.t('validate.email_format')));
    }
};
var validateNumber = (rule, value, callback) => {
    if (/^\d+$/.test(value)) {
        callback();
    } else {
        callback(new Error('必须为数字类型'));
    }
};
var validateFloat = (rule, value, callback) => {
    if (/^([1-9]+(\.\d+)?|0\.\d+)$/.test(value)) {
        callback();
    } else {
        callback(new Error('必须为数字类型'));
    }
};
var validateInterval = (rule, value, callback) => {
	if (/^\d+,\d+$/.test(value)) {
		callback();
	} else {
		callback(new Error('正确格式12,56'));
	}
};
export default {
    mobile: [{
        validator: validateMobile,
        trigger: "blur"
    }],
    name: [{
        required: true,
        message: i18n.t('validate.name'),
        trigger: "blur"
    }],
    zh: [{
        required: true,
        message: i18n.t('validate.required'),
        trigger: "blur"
    }],
    en: [{
        required: true,
        message: i18n.t('validate.required'),
        trigger: "blur"
    }],
    ar: [{
        required: true,
        message: i18n.t('validate.required'),
        trigger: "blur"
    }],
    realName: [{
        required: true,
        message: i18n.t('validate.real_name'),
        trigger: "blur"
    }],
    one688_id: [{
        required: true,
        message: i18n.t('validate.required'),
        trigger: "blur"
    },
    {
        validator: validateNumber,
        trigger: "blur"
    }],
    logistics: [{
        required: true,
        message: i18n.t('validate.required'),
        trigger: "blur"
    },
    {
        validator: validateFloat,
        trigger: "blur"
    }],
    profit_rate: [{
        required: true,
        message: i18n.t('validate.required'),
        trigger: "blur"
    },
    {
        validator: validateFloat,
        trigger: "blur"
    }],
    exchange_rate: [{
    	required: true,
    	message: i18n.t('validate.required'),
    	trigger: "blur"
    },
    {
    	validator: validateFloat,
    	trigger: "blur"
    }],
    discount_parameter: [{
    	required: true,
    	message: i18n.t('validate.required'),
    	trigger: "blur"
    },
    {
    	validator: validateInterval,
    	trigger: "blur"
    }],
    password: [{
            required: true,
            message: i18n.t('validate.password'),
            trigger: "blur"
        },
        {
            min: 8,
            max: 16,
            message: i18n.t('validate.password_length'),
            trigger: "blur"
        }
    ],
    rePassword: [{
            required: true,
            message: i18n.t('validate.password'),
            trigger: "blur"
        },
        {
            min: 8,
            max: 16,
            message: i18n.t('validate.password_length'),
            trigger: "blur"
        },
    ],
    phone: [{
            required: true,
            message: i18n.t('validate.phone'),
            trigger: "blur"
        },
        {
            validator: validateMobile,
            trigger: "blur"
        }
    ],
    email: [{
            required: true,
            message: i18n.t('validate.email'),
            trigger: "blur"
        },
        {
            validator: validateEmail,
            trigger: "blur"
        }
    ],
    description: [{
        required: true,
        message: i18n.t('validate.description'),
        trigger: "blur"
    }],
    groupId: [{
        type: "number",
        required: true,
        message: i18n.t('validate.group_id'),
        trigger: "blur"
    }],
    value: [{
        required: true,
        message: i18n.t('validate.group_id_vule'),
        trigger: "blur"
    }]
}