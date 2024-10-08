/* tslint:disable */
/* eslint-disable */
/**
 * OpenAPI Petstore
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


/**
 * 
 * @export
 */
export const SingleRefType = {
    Admin: 'admin',
    User: 'user'
} as const;
export type SingleRefType = typeof SingleRefType[keyof typeof SingleRefType];


export function instanceOfSingleRefType(value: any): boolean {
    for (const key in SingleRefType) {
        if (Object.prototype.hasOwnProperty.call(SingleRefType, key)) {
            if (SingleRefType[key as keyof typeof SingleRefType] === value) {
                return true;
            }
        }
    }
    return false;
}

export function SingleRefTypeFromJSON(json: any): SingleRefType {
    return SingleRefTypeFromJSONTyped(json, false);
}

export function SingleRefTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): SingleRefType {
    return json as SingleRefType;
}

export function SingleRefTypeToJSON(value?: SingleRefType | null): any {
    return value as any;
}

export function SingleRefTypeToJSONTyped(value: any, ignoreDiscriminator: boolean): SingleRefType {
    return value as SingleRefType;
}

